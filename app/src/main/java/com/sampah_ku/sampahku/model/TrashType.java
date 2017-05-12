package com.sampah_ku.sampahku.model;

/**
 * Created by Faldy on 12/05/2017.
 */

public class TrashType {
    private String trash_type;
    private String icon_path;

    public TrashType(String trash_type, String icon_path) {
        this.trash_type = trash_type;
        this.icon_path = icon_path;
    }

    public String getTrash_type() {
        return trash_type;
    }

    public void setTrash_type(String trash_type) {
        this.trash_type = trash_type;
    }

    public String getIcon_path() {
        return icon_path;
    }

    public void setIcon_path(String icon_path) {
        this.icon_path = icon_path;
    }
}
