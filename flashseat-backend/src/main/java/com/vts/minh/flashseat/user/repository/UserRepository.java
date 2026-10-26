package com.vts.minh.flashseat.user.repository;

import com.vts.minh.flashseat.user.entity.User;
import com.vts.minh.flashseat.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByIdAndStatusNot(Long id,  UserStatus status);

    List<User> findAllByStatusNot(UserStatus status);
}
