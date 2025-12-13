package com.amirkenesbay.asanali_jwt_security.repository;

import com.amirkenesbay.asanali_jwt_security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
