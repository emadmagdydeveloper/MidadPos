package com.midad_pos.model;

import java.io.Serializable;
import java.util.List;

public class WereHouse implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private List<POSModel> pos;
    private POSModel selectedPos;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public List<POSModel> getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    public POSModel getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(POSModel selectedPos) {
        this.selectedPos = selectedPos;
    }
}
