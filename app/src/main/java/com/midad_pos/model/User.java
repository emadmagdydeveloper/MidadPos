package com.midad_pos.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String id;
    private boolean is_admin;
    private String name;
    private String admin_id;
    private String email;
    private String pin_code;
    private String phone;
    private String company_name;
    private AdminModel admin;
    private List<WereHouse> warehouses;
    private String access_token;
    private String token_type;
    private boolean available;

    public String getId() {
        return id;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public String getName() {
        return name;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public String getEmail() {
        return email;
    }

    public String getPin_code() {
        return pin_code;
    }

    public String getPhone() {
        return phone;
    }

    public String getCompany_name() {
        return company_name;
    }

    public AdminModel getAdmin() {
        return admin;
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

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }
}
