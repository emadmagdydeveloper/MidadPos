package com.midad_app_pos.model;

import java.io.Serializable;

public class AdvantageDataModel extends StatusResponse implements Serializable {
    private AdvantageModel data;

    public AdvantageModel getData() {
        return data;
    }
}
