package com.example.newsaggregator2.repositories;

import com.example.newsaggregator2.entities.Keyword;
import com.example.newsaggregator2.entities.News;
import com.example.newsaggregator2.entities.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Optional<News> findByLink(String link);

    @Query("SELECT DISTINCT n FROM News n LEFT JOIN n.keywords k WHERE n.source IN :sources OR k IN :keywords ORDER BY n.publishedAt DESC")
    List<News> findNewsByUserSubscriptions(
            @Param("sources") Set<Source> sources,
            @Param("keywords") Set<Keyword> keywords
    );
}