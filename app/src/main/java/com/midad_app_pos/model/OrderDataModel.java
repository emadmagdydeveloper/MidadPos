package com.midad_app_pos.model;

import java.io.Serializable;
import java.util.List;

public class OrderDataModel extends StatusResponse implements Serializable {
    private List<OrderModel> data;

    public List<OrderModel> getData() {
        return data;
    }
}
