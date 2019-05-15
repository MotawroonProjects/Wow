package com.creativeshare.wow.models;

import java.io.Serializable;
import java.util.List;

public class DepartmentDataModel implements Serializable {
    private List<Department_Model> data;

    public List<Department_Model> getData() {
        return data;
    }
}
