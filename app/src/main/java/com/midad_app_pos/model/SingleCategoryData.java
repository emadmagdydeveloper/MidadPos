package com.midad_app_pos.model;

import java.io.Serializable;

public class SingleCategoryData extends StatusResponse implements Serializable {
    private CategoryModel data;

    public CategoryModel getData() {
        return data;
    }
}
