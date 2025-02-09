package com.example.arrangeit.helpers;
import java.io.Serializable;

public class FurnitureItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String modelUrl;
    private String imageUrl;
    private String colours;
    private String dimensions;
    private String texture;

    public FurnitureItem(String name, String description, double price, String colours, String imageUrl, String modelUrl, String texture, String dimensions) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.colours = colours;
        this.imageUrl = imageUrl;
        this.modelUrl = modelUrl;
        this.texture = texture;
        this.dimensions = dimensions;
    }


    public String getId() {
        return id;

    }
    public String getName() {
        return name;

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
    public String getDimensions() {
        return dimensions;
    }
}
