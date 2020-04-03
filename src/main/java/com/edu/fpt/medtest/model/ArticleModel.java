package com.edu.fpt.medtest.model;

import com.edu.fpt.medtest.entity.Article;

public class ArticleModel extends Article {
    private String creatorName;

    public ArticleModel() {
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
