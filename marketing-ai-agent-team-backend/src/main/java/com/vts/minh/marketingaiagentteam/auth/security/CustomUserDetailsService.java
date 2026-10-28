package com.vts.minh.marketingaiagentteam.auth.security;

import com.vts.minh.marketingaiagentteam.user.entity.User;
import com.vts.minh.marketingaiagentteam.user.enums.UserStatus;
import com.vts.minh.marketingaiagentteam.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        String email = username.trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        if(user.getPasswordHash() == null || user.getPasswordHash().isBlank()){
            throw new UsernameNotFoundException("Invalid credentials");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .disabled(user.getStatus() != UserStatus.ACTIVE).build();
    }
}
