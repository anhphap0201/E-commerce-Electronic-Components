package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.dto.request.UserCreateRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.UserCreateResponse;
import com.example.ecommerceelectroniccomponentsbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/users")
    public UserCreateResponse createUser(@RequestBody UserCreateRequest request){
        return userService.createUser(request);
    }


}
