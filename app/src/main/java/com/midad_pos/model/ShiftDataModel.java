package com.midad_pos.model;

import java.io.Serializable;

public class ShiftDataModel extends StatusResponse implements Serializable {
    private ShiftModel data;

    public ShiftModel getData() {
        return data;
    }
}
