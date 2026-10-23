package com.vts.minh.flashseat.user.internal.repository;

import com.vts.minh.flashseat.user.internal.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface userRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
}
