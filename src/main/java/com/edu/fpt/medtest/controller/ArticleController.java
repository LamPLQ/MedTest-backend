package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Article;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.ArticleModel;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.ArticleService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
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
        try {
            List<Article> listArticle = articleService.listArticle();
            if (listArticle.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có bài viết nào mới!"), HttpStatus.OK);
            }
            List<ArticleModel> lsArticleReturn = new ArrayList<>();
            for (Article lsArticle : listArticle) {
                ArticleModel model = new ArticleModel();
                model.setID(lsArticle.getID());
                model.setContent(lsArticle.getContent());
                model.setShortContent(lsArticle.getShortContent());
                model.setTittle(lsArticle.getTittle());
                model.setImage(lsArticle.getImage());
                //=====================//
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String displayCreatedTest = sdf2.format(lsArticle.getCreatedTime());
                String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                //=====================//
                model.setCreatedTime(createdTime);
                model.setUserID(lsArticle.getUserID());
                model.setCreatorName(userRepository.findById(lsArticle.getUserID()).get().getName());
                lsArticleReturn.add(model);
            }
            return new ResponseEntity<>(lsArticleReturn, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //create new article
    @PostMapping("/create")
    public ResponseEntity<?> createArticle(@RequestBody Article article) {
        try {
            try {
                if (article.getUserID() == 0 || article.getContent().isEmpty() || article.getShortContent().isEmpty() || article.getTittle().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Cần điền đầy đủ các trường trước khi tạo bài viết mới!"), HttpStatus.OK);
                }
            } catch (NullPointerException e) {
                return new ResponseEntity<>(new ApiResponse(false, "Cần điền đầy đủ các trường trước khi tạo bài viết mới!"), HttpStatus.OK);
            }
            if(!userRepository.findById(article.getUserID()).isPresent()){
                return new ResponseEntity<>(new ApiResponse(false, "Người tạo bài viết không tồn tại!"), HttpStatus.OK);
            }
            articleService.saveArticle(article);
            return new ResponseEntity<>(new ApiResponse(true, "Tạo thành công 1 bài viết mới!"), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //detail 1 article
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> editArticle(@PathVariable("id") int id) {
        try {
            Optional<Article> getArticle = articleService.getArticle(id);
            if (!getArticle.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy bài viết nào!"), HttpStatus.OK);
            }
            ArticleModel model = new ArticleModel();
            model.setID(id);
            model.setContent(getArticle.get().getContent());
            model.setShortContent(getArticle.get().getShortContent());
            model.setTittle(getArticle.get().getTittle());
            model.setImage(getArticle.get().getImage());
            //=====================//
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String displayCreatedTest = sdf2.format(getArticle.get().getCreatedTime());
            String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
            //=====================//
            model.setCreatedTime(createdTime);
            model.setUserID(getArticle.get().getUserID());
            model.setCreatorName(userRepository.findById(getArticle.get().getUserID()).get().getName());
            return new ResponseEntity<>(model, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //update 1 article
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateArticle(@RequestBody Article article, @PathVariable("id") int id) {
        try {
            Optional<Article> getArticle = articleService.getArticle(id);
            if (!getArticle.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy bài viết nào!"), HttpStatus.OK);
            }
            article.setID(id);
            articleService.updateArticle(article);
            return new ResponseEntity<>(new ApiResponse(true, "Cập nhật bài viết thành công"), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //delete 1 article
    @PostMapping(value = "/delete/{id}")
    public ResponseEntity deleteArticle(@PathVariable("id") int id) {
        try {
            Optional<Article> getArticle = articleService.getArticle(id);
            if (!getArticle.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy bài viết nào!"), HttpStatus.OK);
            }
            articleService.deleteArticle(id);
            return new ResponseEntity<>(new ApiResponse(true, "Xoá bài viết thành công!"), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }
}
