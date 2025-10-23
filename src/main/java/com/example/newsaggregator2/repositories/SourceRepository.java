package com.example.newsaggregator2.repositories;

import com.example.newsaggregator2.entities.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {
    Optional<Source> findByApiSourceId(String apiSourceId);
}