package com.example.newsaggregator2.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourcesApiResponse {
    private String status;
    private List<SourceApiDto> sources;
}