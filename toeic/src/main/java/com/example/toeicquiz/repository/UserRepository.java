package com.example.toeicquiz.repository;

import com.example.toeicquiz.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository; // ✅ 변경
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> { // ✅ <User, String>

    boolean existsByUserid(String userid);

    Optional<User> findByUserid(String userid);
}
