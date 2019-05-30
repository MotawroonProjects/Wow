package com.creative_share_apps.wow.models;

import java.io.Serializable;
import java.util.List;

public class ProductsDataModel implements Serializable {

    private List<ProductModel> data;
    private Meta meta;

    public List<ProductModel> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public class ProductModel implements Serializable
    {
        private String id_product;
        private String dep_id_fk;
        private String user_id_fk;
        private String ar_title_pro;
        private String en_title_pro;
        private String price;
        private String ar_details_pro;
        private String en_details_pro;
        private String image;
        private String notes;
        private String user_country;



        public String getId_product() {
            return id_product;
        }

        public String getDep_id_fk() {
            return dep_id_fk;
        }

        public String getUser_id_fk() {
            return user_id_fk;
        }

        public String getAr_title_pro() {
            return ar_title_pro;
        }

        public String getEn_title_pro() {
            return en_title_pro;
        }

        public String getPrice() {
            return price;
        }

        public String getAr_details_pro() {
            return ar_details_pro;
        }

        public String getEn_details_pro() {
            return en_details_pro;
        }

        public String getImage() {
            return image;
        }

        public String getNotes() {
            return notes;
        }

        public String getUser_country() {
            return user_country;
        }
    }

    public class Meta implements Serializable
    {
        private int current_page;

        public int getCurrent_page() {
            return current_page;
        }
    }

}
