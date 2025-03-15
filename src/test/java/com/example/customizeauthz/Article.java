package com.example.customizeauthz;

import java.util.Objects;

public class Article {
    private int id;
    private String title;

    public Article() {
    }

    public Article(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        Article article = (Article) o;
        return id==article.id && Objects.equals(title, article.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}