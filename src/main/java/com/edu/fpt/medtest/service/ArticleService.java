package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Article;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ArticleService {
    void saveArticle(Article article);

    List<Article> listArticle();

    Optional<Article> getArticle(int id);

    void deleteArticle(int id);

    void updateArticle(Article article);
}
