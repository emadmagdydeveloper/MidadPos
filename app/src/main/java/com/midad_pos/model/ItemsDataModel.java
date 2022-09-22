package com.midad_pos.model;

import java.io.Serializable;
import java.util.List;

public class ItemsDataModel extends StatusResponse implements Serializable {
    private List<ItemModel> data;

    public List<ItemModel> getData() {
        return data;
    }
}
