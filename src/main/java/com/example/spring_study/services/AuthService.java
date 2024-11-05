package com.example.spring_study.services;

import com.example.spring_study.model.payload.LoginRequest;

public interface AuthService {
    String login(LoginRequest request);
}
