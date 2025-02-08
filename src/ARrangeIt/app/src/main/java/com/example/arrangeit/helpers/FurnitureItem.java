package com.example.arrangeit.helpers;
import java.io.Serializable;

public class FurnitureItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String image;
    private String model;

    public FurnitureItem(String name, String description, double price, String image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
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
    public String getImage() {
        return image;
    }

    public String getModel() {
        return model;
    }
}
