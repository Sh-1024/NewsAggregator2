package com.example.newsaggregator2.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceApiDto {
    private String id;
    private String name;
    private String description;
}