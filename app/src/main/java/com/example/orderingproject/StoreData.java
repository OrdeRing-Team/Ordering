package com.example.orderingproject;

public class StoreData {
    int image;
    String title;
    String content;
    String score;


    public StoreData(int image, String title, String score, String content) {
        this.image = image;
        this.title = title;
        this.score = score;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

}
