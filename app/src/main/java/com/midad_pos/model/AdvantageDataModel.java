package com.midad_pos.model;

import java.io.Serializable;
import java.util.List;

public class AdvantageDataModel extends StatusResponse implements Serializable {
    private AdvantageModel data;

    public AdvantageModel getData() {
        return data;
    }
}
