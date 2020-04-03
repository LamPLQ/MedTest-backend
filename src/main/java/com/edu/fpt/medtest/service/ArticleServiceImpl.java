package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Article;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleRepository articleRepository;


    @Override
    public void saveArticle(Article article) {
        articleRepository.save(article);
    }

    @Override
    public List<Article> listArticle() {
        List<Article> listArticle = articleRepository.findAllByOrderByCreatedTimeDesc();
        return listArticle;
    }

    @Override
    public Optional<Article> getArticle(int id) {
        Optional<Article> getArticle = articleRepository.findById(id);
        return getArticle;
    }

    @Override
    public void deleteArticle(int id) {
        articleRepository.deleteById(id);
    }

    @Override
    public void updateArticle(Article article) {
        Article articleByID = articleRepository.findById(article.getID()).get();
        articleByID.setContent(article.getContent());
        articleByID.setShortContent(article.getShortContent());
        articleByID.setTittle(article.getTittle());
        articleByID.setImage(article.getImage());
        articleRepository.save(articleByID);
    }
}
