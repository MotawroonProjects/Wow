package com.creative_share_apps.wow.models;

import java.io.Serializable;
import java.util.List;

public class OrderModelToUpload implements Serializable {

    private String client_id;
    private double total_order_cost;
    private double client_lat;
    private double client_lng;
    private String client_address;
    private String family_id;

    private List<ItemModel> products;

    public OrderModelToUpload(String client_id, double total_order_cost, double client_lat, double client_lng, String client_address,String family_id, List<ItemModel> products) {
        this.client_id = client_id;
        this.total_order_cost = total_order_cost;
        this.client_lat = client_lat;
        this.client_lng = client_lng;
        this.client_address = client_address;
        this.family_id = family_id;
        this.products = products;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public double getTotal_order_cost() {
        return total_order_cost;
    }

    public void setTotal_order_cost(double total_order_cost) {
        this.total_order_cost = total_order_cost;
    }

    public List<ItemModel> getProducts() {
        return products;
    }

    public void setProducts(List<ItemModel> products) {
        this.products = products;
    }

    public double getClient_lat() {
        return client_lat;
    }

    public double getClient_lng() {
        return client_lng;
    }

    public String getClient_address() {
        return client_address;
    }

    public void setClient_lat(double client_lat) {
        this.client_lat = client_lat;
    }

    public void setClient_lng(double client_lng) {
        this.client_lng = client_lng;
    }

    public void setClient_address(String client_address) {
        this.client_address = client_address;
    }

    public String getFamily_id() {
        return family_id;
    }
}
