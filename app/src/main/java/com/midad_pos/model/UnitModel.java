package com.midad_pos.model;

import java.io.Serializable;

public class UnitModel implements Serializable {
    private String id;
    private String user_id;
    private String unit_code;
    private String unit_name;
    private String base_unit;
    private String operator;
    private String operation_value;
    private String is_active;
    private String created_at;
    private String updated_at;
    private boolean isChecked = false;

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUnit_code() {
        return unit_code;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public String getBase_unit() {
        return base_unit;
    }

    public String getOperator() {
        return operator;
    }

    public String getOperation_value() {
        return operation_value;
    }

    public String getIs_active() {
        return is_active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
