package com.creative_share_apps.wow.models;

import java.io.Serializable;

public class Department_Model implements Serializable {

    private String id_department;
    private String ar_title_dep;
    private String en_title_dep;
    private String user_id_fk;


    public Department_Model(String id_department, String ar_title_dep, String en_title_dep, String user_id_fk) {
        this.id_department = id_department;
        this.ar_title_dep = ar_title_dep;
        this.en_title_dep = en_title_dep;
        this.user_id_fk = user_id_fk;
    }

    public String getId_department() {
        return id_department;
    }

    public String getAr_title_dep() {
        return ar_title_dep;
    }

    public String getEn_title_dep() {
        return en_title_dep;
    }

    public String getUser_id_fk() {
        return user_id_fk;
    }
}
