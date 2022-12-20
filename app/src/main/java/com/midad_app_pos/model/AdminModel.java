package com.midad_app_pos.model;

import java.io.Serializable;
import java.util.List;

public class AdminModel implements Serializable {
    private String id;
    private boolean is_admin;
    private String name;
    private String email;
    private String phone;
    private String pin_code;
    private String company_name;
    private List<WereHouse> warehouses;
    private String access_token;
    private String token_type;

    public String getId() {
        return id;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPin_code() {
        return pin_code;
    }

    public String getCompany_name() {
        return company_name;
    }

    public List<WereHouse> getWarehouses() {
        return warehouses;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }
}
