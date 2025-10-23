package com.example.newsaggregator2.controllers;
import com.example.newsaggregator2.dto.KeywordDto;
import com.example.newsaggregator2.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final UserService userService;

    @GetMapping
    public String showKeywordsPage(Model model, Principal principal) {
        String login = principal.getName();
        model.addAttribute("userKeywords", userService.getKeywordsForUser(login));
        model.addAttribute("keywordDto", new KeywordDto());
        return "keywords";
    }

    @PostMapping("/add")
    public String addKeyword(@Valid @ModelAttribute("keywordDto") KeywordDto keywordDto,
                             BindingResult result,
                             Principal principal,
                             Model model) {
        String login = principal.getName();

        if (result.hasErrors()) {
            model.addAttribute("userKeywords", userService.getKeywordsForUser(login));
            return "keywords";
        }

        try {
            userService.addKeywordToUser(login, keywordDto.getName());
        } catch (IllegalStateException e) {
            model.addAttribute("maxKeywordsError", e.getMessage());
            model.addAttribute("userKeywords", userService.getKeywordsForUser(login));
            return "keywords";
        }

        return "redirect:/keywords";
    }
    @PostMapping("/delete")
    public String deleteKeyword(@RequestParam("id") Long keywordId, Principal principal) {
        userService.removeKeywordFromUser(principal.getName(), keywordId);
        return "redirect:/keywords";
    }
}