package com.example.arrangeit.helpers;

import java.util.List;

public class ScreenshotItem {
    private String imageUrl;
    private String name;
    private String date;
    private int modelCount;
    private List<String> furnitureNames;

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getFurnitureNames() { return furnitureNames; }
    public void setFurnitureNames(List<String> furnitureNames) { this.furnitureNames = furnitureNames; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getModelCount() { return modelCount; }
    public void setModelCount(int modelCount) { this.modelCount = modelCount; }
}