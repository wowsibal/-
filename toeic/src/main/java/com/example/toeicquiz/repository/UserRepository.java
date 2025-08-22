package com.example.toeicquiz.repository;

import com.example.toeicquiz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 중복 아이디 체크용
    boolean existsByUserid(String userid);

    // 로그인 시 사용자 조회용
    Optional<User> findByUserid(String userid);
}
