package com.creative_share_apps.wow.models;

import java.io.Serializable;

public class RegisterMeAsModel implements Serializable {

    private String not_type;
    private int status;

    public RegisterMeAsModel(String not_type, int status) {
        this.not_type = not_type;
        this.status = status;
    }

    public String getNot_type() {
        return not_type;
    }

    public int getStatus() {
        return status;
    }
}
