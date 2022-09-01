package com.midad_pos.model;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    private int id;
    private String title;

    public CategoryModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
