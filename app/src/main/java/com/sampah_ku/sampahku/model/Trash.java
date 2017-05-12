package com.sampah_ku.sampahku.model;

/**
 * Created by Faldy on 01/05/2017.
 */

public class Trash {
    private String id;
    private String description;
    private String photo_path;
    private String user_id;
    private String trash_type_id;
    private Double verified;
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private TrashType trash_type;

    public Trash(String id, String description, String photo_path, String user_id, String trash_type_id, Double verified, Double latitude, Double longitude, Double accuracy, TrashType trash_type) {
        this.id = id;
        this.description = description;
        this.photo_path = photo_path;
        this.user_id = user_id;
        this.trash_type_id = trash_type_id;
        this.verified = verified;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.trash_type = trash_type;
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

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTrash_type_id() {
        return trash_type_id;
    }

    public void setTrash_type_id(String trash_type_id) {
        this.trash_type_id = trash_type_id;
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

    public TrashType getTrash_type() {
        return trash_type;
    }

    public void setTrash_type(TrashType trash_type) {
        this.trash_type = trash_type;
    }
}
