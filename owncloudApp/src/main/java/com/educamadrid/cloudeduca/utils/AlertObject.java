package com.educamadrid.cloudeduca.utils;

import com.google.gson.annotations.SerializedName;

public class AlertObject {
    @SerializedName("cloud") Alert alert;

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }
}
