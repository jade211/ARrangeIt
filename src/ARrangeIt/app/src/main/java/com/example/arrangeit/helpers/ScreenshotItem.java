package com.example.arrangeit.helpers;

import java.util.List;

/**
 * Represents saved layout screenshot item
 */
public class ScreenshotItem {
    private String imageUrl;
    private String name;
    private String date;
    private int modelCount;
    private List<String> furnitureNames;

    /**
     * Gets URL of screenshot image.
     * @return (URL pointing to the image file in firebase)
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets URL of the screenshot image.
     * @param imageUrl (URL pointing to the image file in firebase)
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the name of the layout.
     * @return (layout name)
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the layout.
     * @param name (layout name specified by user)
     */
    public void setName(String name) {
        this.name = name;

    }

    /**
     * Gets list of furniture models used in the layout
     * @return (list of furniture models)
     */
    public List<String> getFurnitureNames() {
        return furnitureNames;
    }

    /**
     * Sets list of furniture models in the layout
     * @param furnitureNames (list of furniture)
     */
    public void setFurnitureNames(List<String> furnitureNames) {
        this.furnitureNames = furnitureNames;
    }

    /**
     * Gets the creation date of the layout.
     * @return (date timestamp)
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets creation date of the layout.
     * @param date (date timestamp)
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets count of models in this layout
     * @return (number of 3D models placed)
     */
    public int getModelCount() {
        return modelCount;
    }

    /**
     * Sets the count of models in the layout
     * @param modelCount (Number of 3D models placed)
     */
    public void setModelCount(int modelCount) {
        this.modelCount = modelCount;
    }
}