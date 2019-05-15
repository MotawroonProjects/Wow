package com.creativeshare.wow.models;

import java.io.Serializable;
import java.util.List;

public class FamiliesStoreDataModel implements Serializable {

    private List<FamilyModel> data;
    private Meta meta;
    public List<FamilyModel> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public class FamilyModel implements Serializable {
        private String user_id;
        private String user_phone;
        private String user_phone_code;
        private String user_type;
        private String user_full_name;
        private String user_email;
        private String user_image;
        private String user_google_lat;
        private String user_google_long;
        private String user_gender;
        private String user_age;
        private double rate;
        private double distance;

        private List<Department_Model> my_dep;

        public String getUser_id() {
            return user_id;
        }

        public String getUser_phone() {
            return user_phone;
        }

        public String getUser_phone_code() {
            return user_phone_code;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getUser_full_name() {
            return user_full_name;
        }

        public String getUser_email() {
            return user_email;
        }

        public String getUser_image() {
            return user_image;
        }

        public String getUser_google_lat() {
            return user_google_lat;
        }

        public String getUser_google_long() {
            return user_google_long;
        }

        public String getUser_gender() {
            return user_gender;
        }

        public String getUser_age() {
            return user_age;
        }

        public double getRate() {
            return rate;
        }

        public double getDistance() {
            return distance;
        }

        public List<Department_Model> getMy_dep() {
            return my_dep;
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
