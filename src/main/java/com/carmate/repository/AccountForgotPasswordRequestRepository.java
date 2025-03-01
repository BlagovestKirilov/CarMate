package com.carmate.repository;

import com.carmate.entity.account.AccountForgotPasswordRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountForgotPasswordRequestRepository extends JpaRepository<AccountForgotPasswordRequest, Long> {

    Optional<AccountForgotPasswordRequest> findByEmailAndToken(String email, String token);

    Optional<AccountForgotPasswordRequest> findByToken(String token);
}
