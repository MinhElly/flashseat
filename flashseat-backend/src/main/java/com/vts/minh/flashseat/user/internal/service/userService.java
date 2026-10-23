package com.vts.minh.flashseat.user.internal.service;

import com.vts.minh.flashseat.user.api.userFacade;
import com.vts.minh.flashseat.user.api.userSummary;
import com.vts.minh.flashseat.user.internal.domain.UserEntity;
import com.vts.minh.flashseat.user.internal.dto.createUserRequest;
import com.vts.minh.flashseat.user.internal.dto.updateUserRequest;
import com.vts.minh.flashseat.user.internal.dto.userResponse;
import com.vts.minh.flashseat.user.internal.repository.userRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class userService implements userFacade {
    private final userRepository userRepository;

    public userService(userRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public userResponse createUser(createUserRequest createUserRequest) {
        String email = createUserRequest.email().trim().toLowerCase(Locale.ROOT);
        if(userRepository.existsByEmail(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        UserEntity user = new UserEntity(createUserRequest.fullName().trim(), email);
        UserEntity saved = userRepository.save(user);

        return new userResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail()
        );
    }
    @Transactional
    public userResponse getUserDetail(Long id){
        UserEntity user = findUser(id);
        return new userResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail());
    }

    @Transactional
    public userSummary getUser(Long id){
        UserEntity user = findUser(id);
        return new userSummary(
                user.getId(),
                user.getFullName());
    }
    @Transactional
    public List<userResponse> getAllUser(){
        List<UserEntity> users = userRepository.findAll();
        return users.stream().map(user -> new userResponse(user.getId(),user.getFullName(),user.getEmail())).toList();
    }
    @Transactional
    public userResponse updateUser(Long id, updateUserRequest updateUserRequest) {
        UserEntity user = findUser(id);
        user.setFullName(updateUserRequest.fullName().trim());
        return new userResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail());
    }
    @Transactional
    public userResponse deleteUser(Long id){
        UserEntity user = findUser(id);
        userRepository.delete(user);
        return new userResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }
    private UserEntity findUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    
}
