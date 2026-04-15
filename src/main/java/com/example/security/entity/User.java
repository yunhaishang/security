package com.example.security.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class User {
    private String username;
    private String password;
    private String role;
}
