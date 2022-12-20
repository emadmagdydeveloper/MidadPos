package com.midad_app_pos.model;

import java.io.Serializable;

public class POSModel implements Serializable {
    private String id;
    private String title;
    private boolean is_login;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isIs_login() {
        return is_login;
    }
}
