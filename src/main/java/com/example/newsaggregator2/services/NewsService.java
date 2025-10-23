package com.example.newsaggregator2.services;
import com.example.newsaggregator2.dto.*;
import com.example.newsaggregator2.entities.Source;
import com.example.newsaggregator2.entities.Keyword;
import com.example.newsaggregator2.entities.User;
import com.example.newsaggregator2.entities.News;
import com.example.newsaggregator2.entities.Role;
import com.example.newsaggregator2.dto.NewsApiResponse;
import com.example.newsaggregator2.repositories.NewsRepository;
import com.example.newsaggregator2.repositories.SourceRepository;
import com.example.newsaggregator2.repositories.UserRepository;
import com.kwabenaberko.newsapilib.NewsApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final SourceRepository sourceRepository;
    private final RestTemplate restTemplate;

    @Value("${newsapi.key}")
    private String apiKey;

    @Value("${newsapi.baseurl}")
    private String baseUrl;

    @Value("${user.days.max-count}")
    private int daysToSubtract;

    NewsApiClient newsApiClient = new NewsApiClient(apiKey);

    @Transactional(readOnly = true)
    public List<News> getNewsFromDbForUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User " + login + " not found"));

        Set<Source> userSources = user.getSources();
        Set<Keyword> userKeywords = user.getKeywords();

        if (userSources.isEmpty() && userKeywords.isEmpty()) {
            return Collections.emptyList();
        }

        return newsRepository.findNewsByUserSubscriptions(userSources, userKeywords);
    }

    @Transactional
    public List<News> fetchAndSaveNewNewsForUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User " + login + " not found"));

        String query = buildApiQuery(user.getKeywords(), user.getSources());
        if (query.isEmpty()) {
            return Collections.emptyList();
        }

        String dateFrom = LocalDate.now().minusDays(daysToSubtract).format(DateTimeFormatter.ISO_LOCAL_DATE);

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("q", query)
                .queryParam("language", "en")
                .queryParam("apiKey", apiKey)
                .queryParam("from", dateFrom)
                .queryParam("sortBy", "publishedAt")
                .build()
                .toUriString();

        NewsApiResponse response = restTemplate.getForObject(url, NewsApiResponse.class);

        if (response != null && response.getArticles() != null) {
            return processAndSaveArticles(response.getArticles(), user.getKeywords());
        }

        return Collections.emptyList();
    }

    private List<News> processAndSaveArticles(List<ArticleDto> articles, Set<Keyword> userKeywords) {
        List<News> savedNews = new ArrayList<>();
        for (ArticleDto articleDto : articles) {
            if (newsRepository.findByLink(articleDto.getUrl()).isEmpty()) {
                Source source = findOrCreateSource(articleDto.getSource());

                News news = new News();
                news.setHeader(articleDto.getTitle());
                news.setLink(articleDto.getUrl());
                news.setPublishedAt(articleDto.getPublishedAt());
                news.setSource(source);

                Set<Keyword> matchedKeywords = userKeywords.stream()
                        .filter(keyword -> news.getHeader().toLowerCase().contains(keyword.getName()))
                        .collect(Collectors.toSet());

                news.setKeywords(matchedKeywords);
                savedNews.add(newsRepository.save(news));
            }
        }
        return savedNews;
    }

    private Source findOrCreateSource(SourceDto sourceDto) {
        if (sourceDto == null || sourceDto.getId() == null) {
            return sourceRepository.findById(1L).orElseGet(() -> {
                Source unknown = new Source();
                unknown.setName("Unknown");
                unknown.setApiSourceId("unknown");
                return sourceRepository.save(unknown);
            });
        }

        return sourceRepository.findByApiSourceId(sourceDto.getId())
                .orElseGet(() -> {
                    Source newSource = new Source();
                    newSource.setApiSourceId(sourceDto.getId());
                    newSource.setName(sourceDto.getName());
                    return sourceRepository.save(newSource);
                });
    }

    private String buildApiQuery(Set<Keyword> keywords, Set<Source> sources) {
        String keywordsQuery = keywords.stream()
                .map(Keyword::getName)
                .collect(Collectors.joining(" OR "));

        String sourcesQuery = sources.stream()
                .map(source -> "\"" + source.getName() + "\"")
                .collect(Collectors.joining(" OR "));

        return Stream.of(keywordsQuery, sourcesQuery)
                .filter(s -> s != null && !s.isEmpty())
                .map(s -> "(" + s + ")")
                .collect(Collectors.joining(" AND "));
    }

    public List<SourceApiDto> fetchAllSources() {
        String url = UriComponentsBuilder.fromHttpUrl("https://newsapi.org/v2/sources")
                .queryParam("apiKey", apiKey)
                .toUriString();

        SourcesApiResponse response = restTemplate.getForObject(url, SourcesApiResponse.class);

        if (response != null && "ok".equals(response.getStatus())) {
            return response.getSources();
        }

        return Collections.emptyList();
    }
}
