package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    List<Article> findAllByOrderByCreatedTimeDesc();
}
