package com.creative_share_apps.wow.models;

import java.io.Serializable;
import java.util.List;

public class OrderClientFamilyDataModel implements Serializable {

    private List<OrderModel> data;
    private Meta meta;

    public List<OrderModel> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public class OrderModel implements Serializable
    {
        private String order_id;
        private String order_type;
        private String order_status;
        private String client_id;
        private String client_address;
        private String client_lat;
        private String client_long;
        private String driver_id;
        private String driver_offer;
        private String order_details;
        private String order_movement;
        private String place_lat;
        private String place_long;
        private String place_address;
        private String order_time_arrival;
        private String driver_user_phone;
        private String driver_user_phone_code;
        private String driver_user_full_name;
        private String driver_user_image;
        private String client_user_phone;
        private String client_user_phone_code;
        private String client_user_full_name;
        private String client_user_image;
        private String order_date;
        private double rate;
        private String room_id_fk;
        private String total_order_cost;
        private List<ProductModel> products;
        private String family_order_end;
        private String send_to_drivers;
        private String accept_date;



        public String getOrder_id() {
            return order_id;
        }

        public String getOrder_type() {
            return order_type;
        }

        public String getOrder_status() {
            return order_status;
        }

        public String getClient_id() {
            return client_id;
        }

        public String getClient_address() {
            return client_address;
        }

        public String getClient_lat() {
            return client_lat;
        }

        public String getClient_long() {
            return client_long;
        }

        public String getDriver_id() {
            return driver_id;
        }

        public String getDriver_offer() {
            return driver_offer;
        }

        public String getOrder_details() {
            return order_details;
        }

        public String getOrder_movement() {
            return order_movement;
        }

        public String getPlace_lat() {
            return place_lat;
        }

        public String getPlace_long() {
            return place_long;
        }

        public String getPlace_address() {
            return place_address;
        }

        public String getOrder_time_arrival() {
            return order_time_arrival;
        }

        public String getDriver_user_phone() {
            return driver_user_phone;
        }

        public String getDriver_user_phone_code() {
            return driver_user_phone_code;
        }

        public String getDriver_user_full_name() {
            return driver_user_full_name;
        }

        public String getDriver_user_image() {
            return driver_user_image;
        }

        public String getClient_user_phone() {
            return client_user_phone;
        }

        public String getClient_user_phone_code() {
            return client_user_phone_code;
        }

        public String getClient_user_full_name() {
            return client_user_full_name;
        }

        public String getClient_user_image() {
            return client_user_image;
        }

        public String getOrder_date() {
            return order_date;
        }

        public double getRate() {
            return rate;
        }

        public String getRoom_id_fk() {
            return room_id_fk;
        }

        public String getTotal_order_cost() {
            return total_order_cost;
        }

        public List<ProductModel> getProducts() {
            return products;
        }

        public String getFamily_order_end() {
            return family_order_end;
        }

        public String getSend_to_drivers() {
            return send_to_drivers;
        }

        public String getAccept_date() {
            return accept_date;
        }
    }


    public class ProductModel implements Serializable
    {
        private String id;
        private String order_id_fk;
        private String product_id_fk;
        private String product_amount;
        private String product_price;
        private String image;
        private String user_country;
        private String ar_title_pro;
        private String en_title_pro;




        public String getId() {
            return id;
        }

        public String getOrder_id_fk() {
            return order_id_fk;
        }

        public String getProduct_id_fk() {
            return product_id_fk;
        }

        public String getProduct_amount() {
            return product_amount;
        }

        public String getProduct_price() {
            return product_price;
        }

        public String getImage() {
            return image;
        }

        public String getUser_country() {
            return user_country;
        }

        public String getAr_title_pro() {
            return ar_title_pro;
        }

        public String getEn_title_pro() {
            return en_title_pro;
        }


    }

    public class Meta implements Serializable
    {
        private int current_page;
        private int total_orders;

        public int getCurrent_page() {
            return current_page;
        }

        public int getTotal_orders() {
            return total_orders;
        }
    }
}
