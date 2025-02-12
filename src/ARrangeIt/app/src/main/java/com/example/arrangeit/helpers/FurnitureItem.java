package com.example.arrangeit.helpers;
import java.io.Serializable;

public class FurnitureItem implements Serializable {
    private String id;
    private String name;
    private String type;
    private String description;
    private double price;
    private String modelUrl;
    private String imageUrl;
    private String colours;
    private double height;
    private double width;
    private double depth;

    private String texture;

    public FurnitureItem(String name, String type, String description, double price, String colours, String imageUrl, String modelUrl, String texture, double height, double width, double depth) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
        this.colours = colours;
        this.imageUrl = imageUrl;
        this.modelUrl = modelUrl;
        this.texture = texture;
        this.height = height;
        this.width = width;
        this.depth = depth;
    }


    public String getId() {
        return id;

    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getColours() {
        return colours;
    }

    public String getTexture() {
        return texture;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getDepth() {
        return depth;
    }
}
