package com.carmate.security.util;

import com.carmate.entity.account.Account;
import com.carmate.entity.account.AccountRegistrationRequest;
import com.carmate.enums.RegistrationStatus;
import com.carmate.repository.AccountRegistrationRequestRepository;
import com.carmate.repository.AccountRepository;
import com.carmate.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    @Autowired
    private AccountRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    private final JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Random random;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AccountRegistrationRequestRepository accountRegistrationRequestRepository;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String confirmRegistration(String email, String password, String code) {
        AccountRegistrationRequest accountRegistrationRequest = accountRegistrationRequestRepository
                .findTopByEmailOrderByDateDesc(email).orElseThrow();
        if (encoder.matches(password, accountRegistrationRequest.getPassword())
        && encoder.matches(code, accountRegistrationRequest.getConfirmationCode())) {
            Account newUser = new Account();
            newUser.setEmail(email);
            newUser.setPassword(encoder.encode(password));
            userRepository.save(newUser);
            accountRegistrationRequest.setStatus(RegistrationStatus.CONFIRMED);
            accountRegistrationRequestRepository.save(accountRegistrationRequest);
        } else{
            throw new RuntimeException("Invalid code!");
        }

        return jwtUtil.generateToken(email);
    }

    private void generateAccountRegistrationRequest(String email, String password) {
        AccountRegistrationRequest accountRegistrationRequest = new AccountRegistrationRequest();
        accountRegistrationRequest.setEmail(email);
        accountRegistrationRequest.setPassword(encoder.encode(password));
        String randomNumber = getRandomNumber();
        LOGGER.info(randomNumber);
        emailService.sendEmail(email, "Confirmation code", "Your confirmation code is " + randomNumber);
        accountRegistrationRequest.setConfirmationCode(encoder.encode(randomNumber));
        accountRegistrationRequestRepository.save(accountRegistrationRequest);
    }

    public String register(String email, String password) {
        Optional<Account> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        generateAccountRegistrationRequest(email, password);

        return jwtUtil.generateToken(email);
    }

    public String login(String email, String password) {
        Account user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getToken() != null && jwtUtil.validateToken(user.getToken()) != null) {
            return user.getToken(); // Return existing valid token
        }

        // Generate new token and save
        String newToken = jwtUtil.generateToken(email);
        user.setToken(newToken);
        userRepository.save(user);
        return newToken;
    }

    private String getRandomNumber(){
        return String.valueOf(100000 + random.nextInt(900000));
    }
}
