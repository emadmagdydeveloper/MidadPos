package com.midad_pos.model;

import java.io.Serializable;

public class CustomerModel implements Serializable {
    private String id;
    private String customer_group_id;
    private String user_id;
    private String admin_id;
    private String name;
    private String company_name;
    private String email;
    private String phone_number;
    private String tax_no;
    private String address;
    private String city;
    private String state;
    private String postal_code;
    private String country;
    private String points;
    private String deposit;
    private String expense;
    private String tax_number;
    private String is_active;
    private String created_at;
    private String updated_at;

    public String getId() {
        return id;
    }

    public String getCustomer_group_id() {
        return customer_group_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public String getName() {
        return name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getTax_no() {
        return tax_no;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public String getCountry() {
        return country;
    }

    public String getPoints() {
        return points;
    }

    public String getDeposit() {
        return deposit;
    }

    public String getExpense() {
        return expense;
    }

    public String getTax_number() {
        return tax_number;
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
}
