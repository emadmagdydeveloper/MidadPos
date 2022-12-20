package com.midad_app_pos.model;

import java.io.Serializable;

public class PaymentModel implements Serializable {
    private String id;
    private String name;
    private String type;
    private String user_id;
    private boolean enable = false;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUser_id() {
        return user_id;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
