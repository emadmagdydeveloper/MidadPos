package com.midad_app_pos.model;

import java.io.Serializable;
import java.util.List;

public class InvoiceIdsModel extends StatusResponse implements Serializable {
    private List<Integer> data;

    public List<Integer> getData() {
        return data;
    }
}
