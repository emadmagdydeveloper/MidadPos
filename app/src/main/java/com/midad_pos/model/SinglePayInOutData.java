package com.midad_pos.model;

import java.io.Serializable;

public class SinglePayInOutData extends StatusResponse implements Serializable {
    private ShiftModel.PayInOutModel data;

    public ShiftModel.PayInOutModel getData() {
        return data;
    }
}
