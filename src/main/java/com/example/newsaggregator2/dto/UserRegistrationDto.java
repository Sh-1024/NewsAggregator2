package com.example.newsaggregator2.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotEmpty(message = "Login cant be empty")
    @Size(min = 4, max = 30, message = "Login must contain from 4 up to 30 signs")
    private String login;

    @NotEmpty(message = "Password cant be empty")
    @Size(min = 6, message = "Password must contain at least 6 signs")
    private String password;
}