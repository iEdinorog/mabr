package org.mabr.authenticationservice.repository;

import org.mabr.authenticationservice.entity.SecurityUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SecurityUser, Integer> {

    Optional<SecurityUser> findByUsername(String username);
}
