package com.example.cst2335_finalproject;

public class SavedImageData {
    private String imagePath;
    private String date;

    public SavedImageData(String imagePath, String date) {
        this.imagePath = imagePath;
        this.date = date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDate() {
        return date;
    }
}
