package com.creativeshare.wow.models;

import java.io.Serializable;

public class NotStateModel implements Serializable {

    private String notification_state;
    private String order_type;

    public NotStateModel(String notification_state, String order_type) {
        this.notification_state = notification_state;
        this.order_type = order_type;
    }

    public String getNotification_state() {
        return notification_state;
    }

    public void setNotification_state(String notification_state) {
        this.notification_state = notification_state;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }
}
