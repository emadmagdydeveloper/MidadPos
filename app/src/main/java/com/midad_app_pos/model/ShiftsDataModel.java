package com.midad_app_pos.model;

import java.io.Serializable;
import java.util.List;

public class ShiftsDataModel extends StatusResponse implements Serializable {
    private List<ShiftModel> data;

    public List<ShiftModel> getData() {
        return data;
    }
}
