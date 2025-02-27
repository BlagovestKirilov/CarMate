package com.carmate.security.util;

import com.carmate.entity.account.Account;
import com.carmate.entity.account.AccountRegistrationRequest;
import com.carmate.enums.AccountRoleEnum;
import com.carmate.enums.LanguageEnum;
import com.carmate.enums.RegistrationStatus;
import com.carmate.repository.AccountRegistrationRequestRepository;
import com.carmate.repository.AccountRepository;
import com.carmate.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final Random random;
    private final AccountRegistrationRequestRepository accountRegistrationRequestRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(
            AccountRepository accountRepository,
            PasswordEncoder encoder,
            JwtUtil jwtUtil,
            EmailService emailService,
            Random random,
            AccountRegistrationRequestRepository accountRegistrationRequestRepository
    ) {
        this.accountRepository = accountRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.random = random;
        this.accountRegistrationRequestRepository = accountRegistrationRequestRepository;
    }

    public String confirmRegistration(String email, String password, String code) {
        AccountRegistrationRequest accountRegistrationRequest = accountRegistrationRequestRepository
                .findTopByEmailOrderByDateDesc(email).orElseThrow();
        if (encoder.matches(password, accountRegistrationRequest.getPassword())
        && encoder.matches(code, accountRegistrationRequest.getConfirmationCode())) {
            Account newUser = new Account();
            newUser.setEmail(email);
            newUser.setPassword(encoder.encode(password));
            newUser.setRole(AccountRoleEnum.USER);
            accountRepository.save(newUser);
            accountRegistrationRequest.setStatus(RegistrationStatus.CONFIRMED);
            accountRegistrationRequest.setRole(AccountRoleEnum.USER);
            accountRegistrationRequestRepository.save(accountRegistrationRequest);
        } else{
            throw new RuntimeException("Invalid code!");
        }

        return jwtUtil.generateToken(email, accountRegistrationRequest.getRole().toString(), LanguageEnum.BULGARIAN.toString());
    }

    private AccountRegistrationRequest generateAccountRegistrationRequest(String email, String password) {
        AccountRegistrationRequest accountRegistrationRequest = new AccountRegistrationRequest();
        accountRegistrationRequest.setEmail(email);
        accountRegistrationRequest.setPassword(encoder.encode(password));
        accountRegistrationRequest.setRole(AccountRoleEnum.USER);
        String randomNumber = getRandomNumber();
        LOGGER.info(randomNumber);
        emailService.sendEmail(email, "Confirmation code", "Your confirmation code is " + randomNumber);
        accountRegistrationRequest.setConfirmationCode(encoder.encode(randomNumber));
        accountRegistrationRequestRepository.save(accountRegistrationRequest);
        return accountRegistrationRequest;
    }

    public String register(String email, String password) {
        Optional<Account> existingUser = accountRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        AccountRegistrationRequest accountRegistrationRequest = generateAccountRegistrationRequest(email, password);

        return jwtUtil.generateToken(email, accountRegistrationRequest.getRole().toString(), LanguageEnum.BULGARIAN.toString());
    }

    public String login(String email, String password) {
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getToken() != null && jwtUtil.validateToken(user.getToken()) != null) {
            return user.getToken(); // Return existing valid token
        }

        // Generate new token and save
        String newToken = jwtUtil.generateToken(email, user.getRole().toString(), user.getLanguage().toString());
        user.setToken(newToken);
        accountRepository.save(user);
        return newToken;
    }

    public void logout(){
        Account account = getAccountByPrincipal();
        account.setToken(null);
        account.setFcmToken(null);
        accountRepository.save(account);
    }

    private String getRandomNumber(){
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private Account getAccountByPrincipal(){
        String username = getPrincipalUserName();
        return accountRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String getPrincipalUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
