package com.nutricheck.controller;

import com.nutricheck.dto.UserRequest;
import com.nutricheck.dto.UserResponse;
import com.nutricheck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createNewUser(@RequestBody UserRequest request){
        return ResponseEntity.ok(userService.createNewUser(request));
    }
}
