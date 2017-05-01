package com.sampah_ku.sampahku.model;

/**
 * Created by Faldy on 01/05/2017.
 */

public class Trash {
    private String id;
    private String description;
    private String photoPath;
    private User userCreated;
    private String trashType;
    private Double verified;
    private Double latitude;
    private Double longitude;
    private Double accuracy;

    public Trash(String id, String description, String photoPath, User userCreated, String trashType, Double verified, Double latitude, Double longitude, Double accuracy) {
        this.id = id;
        this.description = description;
        this.photoPath = photoPath;
        this.userCreated = userCreated;
        this.trashType = trashType;
        this.verified = verified;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    public String getTrashType() {
        return trashType;
    }

    public void setTrashType(String trashType) {
        this.trashType = trashType;
    }

    public Double getVerified() {
        return verified;
    }

    public void setVerified(Double verified) {
        this.verified = verified;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }
}
