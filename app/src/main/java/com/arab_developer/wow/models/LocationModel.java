package com.arab_developer.wow.models;

import android.location.Location;

import java.io.Serializable;

public class LocationModel implements Serializable {

   static Location location;

    public static Location getLocation() {
        return location;
    }

    public static void setLocation(Location location) {
        LocationModel.location = location;
    }
}
