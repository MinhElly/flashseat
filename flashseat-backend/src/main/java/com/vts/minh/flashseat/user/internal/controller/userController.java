package com.vts.minh.flashseat.user.internal.controller;

import com.vts.minh.flashseat.user.internal.domain.UserEntity;
import com.vts.minh.flashseat.user.internal.dto.createUserRequest;
import com.vts.minh.flashseat.user.internal.dto.updateUserRequest;
import com.vts.minh.flashseat.user.internal.dto.userResponse;
import com.vts.minh.flashseat.user.internal.service.userService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class userController {
    private final userService userService;

    public userController(userService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<userResponse> createUser(@Valid @RequestBody createUserRequest request){
        userResponse user = userService.createUser(request);
        URI location = URI.create("/api/user/" + user.id());
        return ResponseEntity.created(location).body(user);
    }
    @GetMapping("/{id}")
    public ResponseEntity<userResponse> getUser(@PathVariable Long id){
        userResponse user = userService.getUserDetail(id);
        return ResponseEntity.ok(user);
    }
    @GetMapping
    public ResponseEntity<List<userResponse>> getAllUsers(){
        List<userResponse> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<userResponse> updateUser(@PathVariable Long id, @Valid @RequestBody updateUserRequest request){
        userResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User delete successfully");
    }
}
