package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Article;
import com.edu.fpt.medtest.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    //get list
    @GetMapping("/list")
    public ResponseEntity<?> listArticle() {
        List<Article> listArticle = articleService.listArticle();
        if (listArticle.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No article available"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(listArticle, HttpStatus.OK);
    }


    //create new article
    @PostMapping("/create")
    public ResponseEntity<?> createArticle(@RequestBody Article article) {
        articleService.saveArticle(article);
        return new ResponseEntity<>(new ApiResponse(true, "Successful Create article !"), HttpStatus.OK);
    }

    //detail 1 article
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> editArticle(@PathVariable("id") int id) {
        Optional<Article> getArticle = articleService.getArticle(id);
        if (!getArticle.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Article not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getArticle, HttpStatus.OK);
    }

    //update 1 article
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateArticle(@RequestBody Article article, @PathVariable("id") int id) {
        Optional<Article> getArticle = articleService.getArticle(id);
        if (!getArticle.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Article not found"), HttpStatus.NOT_FOUND);
        }
        article.setID(id);
        articleService.saveArticle(article);
        return new ResponseEntity<>(new ApiResponse(true, "Article update successfully"), HttpStatus.OK);
    }
}
