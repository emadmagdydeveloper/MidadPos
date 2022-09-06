package com.midad_pos.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusResponse implements Serializable {
    @SerializedName(value = "code",alternate = {"status"})
    protected int status;
    protected String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
