package com.example.security.controller;

import com.example.security.entity.User;
import com.example.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public User getInfo() {
        return userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
