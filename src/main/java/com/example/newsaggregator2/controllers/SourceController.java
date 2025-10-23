package com.example.newsaggregator2.controllers;
import com.example.newsaggregator2.dto.SourceApiDto;
import com.example.newsaggregator2.entities.Source;
import com.example.newsaggregator2.entities.User;
import com.example.newsaggregator2.services.NewsService;
import com.example.newsaggregator2.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sources")
@RequiredArgsConstructor
public class SourceController {

    private final UserService userService;
    private final NewsService newsService;

    @GetMapping
    public String showSourcesPage(Model model, Principal principal) {
        String login = principal.getName();
        User user = userService.findUserByLogin(login).get();

        List<SourceApiDto> allSources = newsService.fetchAllSources();
        List<Source> userSources = List.copyOf(user.getSources());

        List<SourceApiDto> availableSources = allSources.stream()
                .filter(apiSource -> userSources.stream().noneMatch(userSource -> userSource.getApiSourceId().equals(apiSource.getId())))
                .collect(Collectors.toList());

        model.addAttribute("userSources", userSources);
        model.addAttribute("availableSources", availableSources);

        return "sources";
    }

    @PostMapping("/add")
    public String addSource(@RequestParam("apiSourceId") String apiSourceId,
                            @RequestParam("sourceName") String sourceName,
                            Principal principal, Model model) {
        try {
            userService.addSourceToUser(principal.getName(), apiSourceId, sourceName);
        } catch (IllegalStateException e) {
            model.addAttribute("maxSourcesError", e.getMessage());
            return showSourcesPage(model, principal);
        }
        return "redirect:/sources";
    }

    @PostMapping("/delete")
    public String deleteSource(@RequestParam("id") Long sourceId, Principal principal) {
        userService.removeSourceFromUser(principal.getName(), sourceId);
        return "redirect:/sources";
    }
}