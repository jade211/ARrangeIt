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

    public FurnitureItem(String name, String description, double price, String colours, String dimensions, String imageUrl, String modelUrl, String texture) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.colours = colours;
        this.dimensions = dimensions;
        this.imageUrl = imageUrl;
        this.modelUrl = modelUrl;
        this.texture = texture;

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
        return modelUrl;
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
