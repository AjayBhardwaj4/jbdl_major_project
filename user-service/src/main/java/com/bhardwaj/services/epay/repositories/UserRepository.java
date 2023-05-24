package com.bhardwaj.services.epay.repositories;

import com.bhardwaj.services.epay.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhoneNumber(String phoneNumber);
}
