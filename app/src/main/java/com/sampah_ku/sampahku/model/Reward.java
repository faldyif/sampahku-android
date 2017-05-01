package com.sampah_ku.sampahku.model;

/**
 * Created by Faldy on 01/05/2017.
 */

public class Reward {
    private String id;
    private String name;
    private String description;
    private String title;
    private Boolean available;
    private Boolean inStock;
    private String photoPath;

    public Reward(String id, String name, String description, String title, Boolean available, Boolean inStock, String photoPath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.title = title;
        this.available = available;
        this.inStock = inStock;
        this.photoPath = photoPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
