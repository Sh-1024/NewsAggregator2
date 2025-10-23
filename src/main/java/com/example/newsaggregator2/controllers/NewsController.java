package com.example.newsaggregator2.controllers;

import com.example.newsaggregator2.dto.NewsDto;
import com.example.newsaggregator2.entities.News;
import com.example.newsaggregator2.services.NewsService;
import com.example.newsaggregator2.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final UserService userService;

    private static final int NEWS_PER_PAGE = 15;

    @GetMapping
    public String getNewsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "db") String type,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<NewsDto> newsList;
        Pageable pageable = PageRequest.of(page, NEWS_PER_PAGE);

        if ("api".equalsIgnoreCase(type)) {
            List<News> fetchedNews = newsService.fetchAndSaveNewNewsForUser(username);
            newsList = newsService.getNewsFromDbForUser(username).stream()
                    .map(NewsDto::fromEntity)
                    .collect(Collectors.toList());
        } else {
            newsList = newsService.getNewsFromDbForUser(username).stream()
                    .map(NewsDto::fromEntity)
                    .collect(Collectors.toList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), newsList.size());
        List<NewsDto> pagedNews = newsList.subList(start, end);

        model.addAttribute("news", pagedNews);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) newsList.size() / NEWS_PER_PAGE));
        model.addAttribute("type", type);

        return "news";
    }

    @GetMapping("/fetch-and-display")
    public String fetchAndDisplayNews(@RequestParam(defaultValue = "0") int page, Model model) {
        return getNewsPage(page, "api", model);
    }
}