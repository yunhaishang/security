package com.example.security.controller;

import com.example.security.dto.LoginRequest;
import com.example.security.dto.RegisterRequest;
import com.example.security.entity.User;
import com.example.security.service.UserService;
import com.example.security.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");
        userService.insertUser(user);
    }

    // login 手动调用 authenticationManager 的 authenticate 方法开始认证
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        // manager的 authenticate方法调用 provider的 authenticate方法
        Authentication authentication = authenticationManager.authenticate(
                // UsernamePasswordAuthenticationToken 构造方法传入2个参数代表待认证(类有属性 authenticated 设置为 false)
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        return jwtUtils.generateToken(authentication.getName());
    }

    @PostMapping("/logout")
    public void logout() {

    }
}
