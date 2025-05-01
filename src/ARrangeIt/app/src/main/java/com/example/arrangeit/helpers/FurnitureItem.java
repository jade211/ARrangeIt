package com.example.arrangeit.helpers;
import java.io.Serializable;

/**
 * Represents a furniture item
 */
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


    /**
     * Constructs a new FurnitureItem with all properties.
     *
     * @param name (name of furniture item)
     * @param type (category of the item)
     * @param description (description of the item)
     * @param price (price listed for the item)
     * @param colours (colour options for the item)
     * @param imageUrl (image location in firebase)
     * @param modelUrl (model location in firebase)
     * @param texture (texture of item)
     * @param height (height of item in cm)
     * @param width (width of item in cm)
     * @param depth (depth of item in cm)
     */
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

    /**
     * @return id of the item
     */
    public String getId() {
        return id;

    }

    /**
     * @return name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * @return type of the item
     */
    public String getType() {
        return type;
    }

    /**
     * @return description of the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return price of the item
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return model location of the item
     */
    public String getModelUrl() {
        return modelUrl;
    }

    /**
     * @return image location of the item
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @return colours of the item
     */
    public String getColours() {
        return colours;
    }

    /**
     * @return texture of the item
     */
    public String getTexture() {
        return texture;
    }

    /**
     * @return height of the item
     */
    public double getHeight() {
        return height;
    }

    /**
     * @return width of the item
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return depth of the item
     */
    public double getDepth() {
        return depth;
    }
}
