package com.example.newsaggregator2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleDto {
    private SourceDto source;
    private String title;
    private String url;
    private OffsetDateTime publishedAt;
}
