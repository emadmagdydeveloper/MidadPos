package com.midad_pos.model;

import java.io.Serializable;
import java.util.List;

public class DiscountDataModel extends StatusResponse implements Serializable {
    private List<DiscountModel> data;

    public List<DiscountModel> getData() {
        return data;
    }
}
