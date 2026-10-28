package com.vts.minh.marketingaiagentteam.user.controller;

import com.vts.minh.marketingaiagentteam.user.dto.request.CreateUserRequest;
import com.vts.minh.marketingaiagentteam.user.dto.request.UpdateUserRequest;
import com.vts.minh.marketingaiagentteam.user.dto.response.UserResponse;
import com.vts.minh.marketingaiagentteam.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User Management API")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content())
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request){
        UserResponse user = userService.createUser(request);
        URI location = URI.create("/api/user/" + user.id());
        return ResponseEntity.created(location).body(user);
    }
    @Operation(summary = "Get user by Id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
        UserResponse user = userService.getUserDetail(id);
        return ResponseEntity.ok(user);
    }
    @GetMapping
    @Operation(summary = "Get all user")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }
    @PatchMapping("/{id}")
    @Operation(summary = "Update user by id")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request){
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete user")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User delete successfully");
    }
}
