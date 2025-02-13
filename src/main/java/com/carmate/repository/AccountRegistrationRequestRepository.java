package com.carmate.repository;

import com.carmate.entity.account.AccountRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRegistrationRequestRepository extends JpaRepository<AccountRegistrationRequest, Long> {
    Optional<AccountRegistrationRequest> findTopByEmailOrderByDateDesc(String email);
}
