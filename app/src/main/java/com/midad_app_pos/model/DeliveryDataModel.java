package com.midad_app_pos.model;

import java.io.Serializable;
import java.util.List;

public class DeliveryDataModel extends StatusResponse implements Serializable {
    private List<DeliveryModel> data;

    public List<DeliveryModel> getData() {
        return data;
    }
}
