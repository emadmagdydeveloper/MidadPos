package com.midad_pos.model;

import java.io.Serializable;

public class SingleCustomerModel extends StatusResponse implements Serializable {
    private CustomerModel data;

    public CustomerModel getData() {
        return data;
    }
}
