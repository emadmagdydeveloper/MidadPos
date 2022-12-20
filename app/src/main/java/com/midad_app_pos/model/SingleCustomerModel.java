package com.midad_app_pos.model;

import java.io.Serializable;

public class SingleCustomerModel extends StatusResponse implements Serializable {
    private CustomerModel data;

    public CustomerModel getData() {
        return data;
    }
}
