package com.example.newsaggregator2.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class KeywordDto {

    @NotEmpty(message = "Keyword cant be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Keyword can contain only English letters")
    private String name;
}