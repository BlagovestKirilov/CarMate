package com.carmate.service;

import com.carmate.config.security.JwtUtil;
import com.carmate.entity.account.Account;
import com.carmate.entity.account.AccountForgotPasswordRequest;
import com.carmate.entity.account.AccountRegistrationRequest;
import com.carmate.enums.AccountRoleEnum;
import com.carmate.enums.ForgotPasswordStatus;
import com.carmate.enums.LanguageEnum;
import com.carmate.enums.RegistrationStatus;
import com.carmate.repository.AccountForgotPasswordRequestRepository;
import com.carmate.repository.AccountRegistrationRequestRepository;
import com.carmate.repository.AccountRepository;
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
    private final AccountForgotPasswordRequestRepository accountForgotPasswordRequestRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(
            AccountRepository accountRepository,
            PasswordEncoder encoder,
            JwtUtil jwtUtil,
            EmailService emailService,
            Random random,
            AccountRegistrationRequestRepository accountRegistrationRequestRepository,
            AccountForgotPasswordRequestRepository accountForgotPasswordRequestRepository) {
        this.accountRepository = accountRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.random = random;
        this.accountRegistrationRequestRepository = accountRegistrationRequestRepository;
        this.accountForgotPasswordRequestRepository = accountForgotPasswordRequestRepository;
    }

    public void confirmRegistration(String email, String password, String code) {
        AccountRegistrationRequest accountRegistrationRequest = accountRegistrationRequestRepository
                .findTopByEmailOrderByDateDesc(email).orElseThrow();
        if (encoder.matches(password, accountRegistrationRequest.getPassword())
                && encoder.matches(code, accountRegistrationRequest.getConfirmationCode())) {
            Account newUser = new Account();
            newUser.setEmail(email);
            newUser.setAccountName(accountRegistrationRequest.getAccountName());
            newUser.setPassword(encoder.encode(password));
            newUser.setRole(AccountRoleEnum.USER);
            accountRepository.save(newUser);
            accountRegistrationRequest.setStatus(RegistrationStatus.CONFIRMED);
            accountRegistrationRequest.setRole(AccountRoleEnum.USER);
            accountRegistrationRequestRepository.save(accountRegistrationRequest);
        } else {
            throw new RuntimeException("Invalid code!");
        }
    }


    private AccountRegistrationRequest generateAccountRegistrationRequest(String email, String password, String accountName) {
        AccountRegistrationRequest accountRegistrationRequest = new AccountRegistrationRequest();
        accountRegistrationRequest.setEmail(email);
        accountRegistrationRequest.setAccountName(accountName);
        accountRegistrationRequest.setPassword(encoder.encode(password));
        accountRegistrationRequest.setRole(AccountRoleEnum.USER);
        String randomNumber = getRandomNumber();
        emailService.sendEmail(email, "Confirmation code", "Your confirmation code is " + randomNumber);
        accountRegistrationRequest.setConfirmationCode(encoder.encode(randomNumber));
        accountRegistrationRequestRepository.save(accountRegistrationRequest);
        return accountRegistrationRequest;
    }

    public String register(String email, String password, String accountName) {
        Optional<Account> existingUser = accountRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        AccountRegistrationRequest accountRegistrationRequest = generateAccountRegistrationRequest(email, password, accountName);

        return jwtUtil.generateToken(email, accountRegistrationRequest.getRole().toString(), LanguageEnum.BULGARIAN.toString());
    }

    public String login(String email, String password) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!encoder.matches(password, account.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (account.getToken() != null && jwtUtil.validateToken(account.getToken()) != null) {
            return account.getToken();
        }

        String newToken = jwtUtil.generateToken(email, account.getRole().toString(), account.getLanguage().toString());
        account.setToken(newToken);
        accountRepository.save(account);
        LOGGER.info("{} logged in", account.getEmail());
        return newToken;
    }

    public void logout() {
        Account account = getAccountByPrincipal();
        account.setToken(null);
        account.setNotificationToken(null);
        LOGGER.info("{} logged out", account.getEmail());
        accountRepository.save(account);
    }

    public String forgotPassword(String email) {
        Optional<Account> existingUser = accountRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        AccountForgotPasswordRequest accountForgotPasswordRequest = new AccountForgotPasswordRequest();
        accountForgotPasswordRequest.setEmail(email);
        String randomNumber = getRandomNumber();
        if (existingUser.get().getLanguage().equals(LanguageEnum.BULGARIAN)) {
            emailService.sendEmail(email, "Забравена парола", "Вашият код за потвърждение е " + randomNumber);
        } else {
            emailService.sendEmail(email, "Forgot password", "Your confirmation code is " + randomNumber);
        }

        accountForgotPasswordRequest.setConfirmationCode(encoder.encode(randomNumber));
        String token = jwtUtil.generateToken(email, existingUser.get().getRole().toString(), existingUser.get().getLanguage().toString());
        accountForgotPasswordRequest.setToken(token);
        accountForgotPasswordRequestRepository.save(accountForgotPasswordRequest);
        LOGGER.info("AccountForgotPasswordRequest saved for: {}", email);
        return token;
    }

    public String confirmForgotPassword(String email, String code, String authorizationHeader) {
        Optional<Account> existingUser = accountRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        }

        if (token == null && jwtUtil.validateToken(token) == null) {
            throw new RuntimeException("Not a valid token");
        }

        AccountForgotPasswordRequest accountForgotPasswordRequest = accountForgotPasswordRequestRepository
                .findByEmailAndToken(email, token).orElseThrow();

        if (!encoder.matches(code, accountForgotPasswordRequest.getConfirmationCode())) {
            throw new RuntimeException("Confirmation code is incorrect");
        }

        String newToken = jwtUtil.generateToken(email, existingUser.get().getRole().toString(), existingUser.get().getLanguage().toString());
        accountForgotPasswordRequest.setToken(newToken);

        accountForgotPasswordRequest.setStatus(ForgotPasswordStatus.CONFIRMED);

        accountForgotPasswordRequestRepository.save(accountForgotPasswordRequest);
        LOGGER.info("AccountForgotPasswordRequest confirmed for: {}", email);
        return newToken;
    }

    public void changePassword(String newPassword, String authorizationHeader) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        if (token == null && jwtUtil.validateToken(token) == null) {
            throw new RuntimeException("Not a valid token!");
        }

        AccountForgotPasswordRequest accountForgotPasswordRequest = accountForgotPasswordRequestRepository.findByToken(token).orElseThrow();

        if (!accountForgotPasswordRequest.getStatus().equals(ForgotPasswordStatus.CONFIRMED)) {
            throw new RuntimeException("Request not confirmed!");
        }

        Account existingAccount = accountRepository.findByEmail(accountForgotPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        accountForgotPasswordRequest.setToken(null);
        existingAccount.setPassword(encoder.encode(newPassword));
        accountRepository.save(existingAccount);
        accountForgotPasswordRequestRepository.save(accountForgotPasswordRequest);
        LOGGER.info("Password changed for account with email: {}", existingAccount.getEmail());
    }

    public void deleteAccount() {
        Account account = getAccountByPrincipal();
        accountRepository.delete(account);
        LOGGER.info("Deleted account with email: {}", account.getEmail());
    }

    private String getRandomNumber() {
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public Account getAccountByPrincipal() {
        String username = getPrincipalUserName();
        return accountRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String getPrincipalUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
