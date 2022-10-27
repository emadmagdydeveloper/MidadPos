package com.midad_pos.model;

import java.io.Serializable;

public class DeliveryModel implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
