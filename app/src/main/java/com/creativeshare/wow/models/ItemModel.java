package com.creativeshare.wow.models;

import java.io.Serializable;

public class ItemModel implements Serializable {

    private String product_id;
    private String dept_id;
    private String user_id_fk;
    private String ar_name;
    private String en_name;
    private String image;
    private int quantity;
    private double product_price;
    private double total_cost;
    private String country_code;

    public ItemModel(String product_id, String dept_id, String user_id_fk, String ar_name, String en_name, String image, int quantity, double product_price,double total_cost, String country_code) {
        this.product_id = product_id;
        this.dept_id = dept_id;
        this.user_id_fk = user_id_fk;
        this.ar_name = ar_name;
        this.en_name = en_name;
        this.image = image;
        this.quantity = quantity;
        this.product_price = product_price;
        this.country_code = country_code;
        this.total_cost = total_cost;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getDept_id() {
        return dept_id;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
    }

    public String getUser_id_fk() {
        return user_id_fk;
    }

    public void setUser_id_fk(String user_id_fk) {
        this.user_id_fk = user_id_fk;
    }

    public String getAr_name() {
        return ar_name;
    }

    public void setAr_name(String ar_name) {
        this.ar_name = ar_name;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(double total_cost) {
        this.total_cost = total_cost;
    }

    public double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
}
