// EducaMadrid: Clase para leer archivo JSON de alertas
package com.educamadrid.cloudeduca.utils;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;


import java.util.List;

/**
 * class Alert
 * POJO built from reading the "cloud" object on the Avisos request
 * TODO: comprobar mFrom y mTo, fechas estáticas
 */
public class Alert {

    @SerializedName(value="active") private int mActive;

    @SerializedName(value="from") private String mFrom;
    @SerializedName(value="to") private String mTo;

    @SerializedName(value="title") private String mTitle;
    @SerializedName(value="message") private String mMessage;

    @SerializedName(value="titleBackground") private String mTitleBackground;
    @SerializedName(value="titleColor") private String mTitleColor;

    @SerializedName(value="button") private int mButton;
    @SerializedName(value="buttonText") private String mButtonText;


    public boolean ismActive() {
        return mActive == 1;
    }

    public void setmActive(int mActive) {
        this.mActive = mActive;
    }

    public String getmFrom() {
        return mFrom;
    }

    public void setmFrom(String mFrom) {
        this.mFrom = mFrom;
    }

    public String getmTo() {
        return mTo;
    }

    public void setmTo(String mTo) {
        this.mTo = mTo;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmTitleBackground() {
        return mTitleBackground;
    }

    public void setmTitleBackground(String mTitleBackground) {
        this.mTitleBackground = mTitleBackground;
    }

    public String getmTitleColor() {
        return mTitleColor;
    }

    public void setmTitleColor(String mTitleColor) {
        this.mTitleColor = mTitleColor;
    }

    public boolean ismButton() {
        return mButton == 1;
    }

    public void setmButton(int mButton) {
        this.mButton = mButton;
    }

    public String getmButtonText() {
        return mButtonText;
    }

    public void setmButtonText(String mButtonText) {
        this.mButtonText = mButtonText;
    }
}

