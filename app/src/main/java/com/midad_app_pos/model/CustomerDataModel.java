package com.midad_app_pos.model;

import java.io.Serializable;
import java.util.List;

public class CustomerDataModel extends StatusResponse implements Serializable {
    private List<CustomerModel> data;

    public List<CustomerModel> getData() {
        return data;
    }
}
