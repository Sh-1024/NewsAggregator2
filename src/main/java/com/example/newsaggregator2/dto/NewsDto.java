package com.example.newsaggregator2.dto;
import com.example.newsaggregator2.entities.Keyword;
import com.example.newsaggregator2.entities.News;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class NewsDto {
    private Integer id;
    private String header;
    private String language;
    private String link;
    private OffsetDateTime publishedAt;
    private String sourceName;
    private Set<String> keywords;

    public static NewsDto fromEntity(News news) {
        NewsDto dto = new NewsDto();
        dto.setId(news.getId());
        dto.setHeader(news.getHeader());
        dto.setLanguage(news.getLanguage());
        dto.setLink(news.getLink());
        dto.setPublishedAt(news.getPublishedAt());
        if (news.getSource() != null) {
            dto.setSourceName(news.getSource().getName());
        }
        if (news.getKeywords() != null) {
            dto.setKeywords(news.getKeywords().stream()
                    .map(Keyword::getName)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }
}
