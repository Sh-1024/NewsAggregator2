package com.example.newsaggregator2.services;
import com.example.newsaggregator2.dto.UserRegistrationDto;
import com.example.newsaggregator2.entities.Keyword;
import com.example.newsaggregator2.entities.Role;
import com.example.newsaggregator2.entities.Source;
import com.example.newsaggregator2.entities.User;
import com.example.newsaggregator2.repositories.KeywordRepository;
import com.example.newsaggregator2.repositories.RoleRepository;
import com.example.newsaggregator2.repositories.SourceRepository;
import com.example.newsaggregator2.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final RoleRepository roleRepository;
    private final SourceRepository sourceRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.keywords.max-count}")
    private int maxKeywordsPerUser;

    @Value("${user.sources.max-count}")
    private int maxSourcesPerUser;

    @Transactional
    public User registerNewUser(UserRegistrationDto registrationDto) {
        if (userRepository.findByLogin(registrationDto.getLogin()).isPresent()) {
            throw new IllegalArgumentException("User with that login already exists: " + registrationDto.getLogin());
        }

        User user = new User();
        user.setLogin(registrationDto.getLogin());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword())); // Хешируем пароль

        Role userRole = roleRepository.findByRole("USER")
                .orElseThrow(() -> new EntityNotFoundException("Role 'USER' is not found."));

        user.setRoles(new HashSet<>(java.util.Collections.singletonList(userRole)));

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Set<Keyword> getKeywordsForUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User is not found: " + login));
        return user.getKeywords();
    }

    @Transactional
    public void addKeywordToUser(String login, String keywordName) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User is not found: " + login));

        if (user.getKeywords().size() >= maxKeywordsPerUser) {
            throw new IllegalStateException("Reached max amount of keywords: " + maxKeywordsPerUser);
        }

        String lowerCaseKeyword = keywordName.toLowerCase();

        boolean alreadyHasKeyword = user.getKeywords().stream()
                .anyMatch(keyword -> keyword.getName().equals(lowerCaseKeyword));
        if (alreadyHasKeyword) {
            return;
        }

        Keyword keyword = keywordRepository.findByName(lowerCaseKeyword)
                .orElseGet(() -> {
                    Keyword newKeyword = new Keyword();
                    newKeyword.setName(lowerCaseKeyword);
                    return keywordRepository.save(newKeyword);
                });

        user.getKeywords().add(keyword);
        userRepository.save(user);
    }

    @Transactional
    public void removeKeywordFromUser(String login, Long keywordId) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User is not found: " + login));

        user.getKeywords().removeIf(keyword -> Long.valueOf(keyword.getId()).equals(keywordId));
        userRepository.save(user);
    }

    @Transactional
    public void addSourceToUser(String login, String apiSourceId, String sourceName) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User is not found: " + login));

        if (user.getSources().size() >= maxSourcesPerUser) {
            throw new IllegalStateException("Reached max limit of sources: " + maxSourcesPerUser);
        }

        Source source = sourceRepository.findByApiSourceId(apiSourceId)
                .orElseThrow(() -> new EntityNotFoundException("Source '" + apiSourceId + "' not found."));

        user.getSources().add(source);
        userRepository.save(user);
    }

    @Transactional
    public void removeSourceFromUser(String login, Long sourceId) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + login));

        user.getSources().removeIf(source -> Long.valueOf(source.getId()).equals(sourceId));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
