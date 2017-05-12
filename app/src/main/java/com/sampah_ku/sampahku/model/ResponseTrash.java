package com.sampah_ku.sampahku.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Faldy on 13/05/2017.
 */

public class ResponseTrash {
    private List<Trash> trash = new ArrayList<Trash>();

    public List<Trash> getTrash() {
        return trash;
    }

    public void setTrash(List<Trash> trash) {
        this.trash = trash;
    }
}
