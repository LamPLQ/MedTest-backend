package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Article;
import com.edu.fpt.medtest.model.ArticleModel;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.ArticleService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    //get list
    @GetMapping("/list")
    public ResponseEntity<?> listArticle() {
        List<Article> listArticle = articleService.listArticle();
        if (listArticle.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không có bài viết nào mới!"), HttpStatus.OK);
        }
        List<ArticleModel> lsArticleReturn = new ArrayList<>();
        for(Article lsArticle: listArticle){
            ArticleModel model = new ArticleModel();
            model.setID(lsArticle.getID());
            model.setContent(lsArticle.getContent());
            model.setShortContent(lsArticle.getShortContent());
            model.setTittle(lsArticle.getTittle());
            model.setImage(lsArticle.getImage());
            model.setCreatedTime(lsArticle.getCreatedTime());
            model.setUserID(lsArticle.getUserID());
            model.setCreatorName(userRepository.findById(lsArticle.getUserID()).get().getName());
            lsArticleReturn.add(model);
        }
        return new ResponseEntity<>(lsArticleReturn, HttpStatus.OK);
    }


    //create new article
    @PostMapping("/create")
    public ResponseEntity<?> createArticle(@RequestBody Article article) {
        articleService.saveArticle(article);
        return new ResponseEntity<>(new ApiResponse(true, "Tạo thành công 1 bài viết mới!"), HttpStatus.OK);
    }

    //detail 1 article
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> editArticle(@PathVariable("id") int id) {
        Optional<Article> getArticle = articleService.getArticle(id);
        if (!getArticle.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy bài viết nào!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(getArticle, HttpStatus.OK);
    }

    //update 1 article
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateArticle(@RequestBody Article article, @PathVariable("id") int id) {
        Optional<Article> getArticle = articleService.getArticle(id);
        if (!getArticle.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy bài viết nào!"), HttpStatus.OK);
        }
        article.setID(id);
        articleService.updateArticle(article);
        return new ResponseEntity<>(new ApiResponse(true, "Cập nhật bài viết thành công"), HttpStatus.OK);
    }

    //delete 1 article
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteArticle(@PathVariable("id") int id) {
        Optional<Article> getArticle = articleService.getArticle(id);
        if (!getArticle.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy bài viết nào!"), HttpStatus.OK);
        }
        articleService.deleteArticle(id);
        return new ResponseEntity<>(new ApiResponse(true, "Xoá bài viết thành công!"), HttpStatus.OK);
    }
}
