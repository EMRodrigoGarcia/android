// EducaMadrid: Clase para leer archivo JSON de alertas
//package com.owncloud.android.utils;
//
//        import com.google.gson.annotations.SerializedName;
//        import org.threeten.bp.LocalDateTime;
//        import org.threeten.bp.format.DateTimeFormatter;
//
//        import java.util.List;
//
///**
// * class Alert
// * Object that is built by reading the JSON
// */
//public class Alert {
//    @SerializedName(value="active") private boolean mActive;
//    @SerializedName(value="from") private String mFrom;
//    @SerializedName(value="to") private String mTo;
//    @SerializedName(value="apps") private List<String> mApps;
//    @SerializedName(value="closable") private Boolean mClosable;
//    @SerializedName(value="type") private String mType;
//    @SerializedName(value="title") private String mTitle;
//    @SerializedName(value="content") private String mContent;
//
//
//    /**
//     * method ismActive
//     * @return active
//     * Method that check if alert is active
//     */
//    public boolean ismActive() {
//        boolean active = false;
//        try {
//            active = mActive;
//        } catch(NullPointerException e) {
//            active = false;
//        }
//        return active;
//    }
//
//
//    /**
//     * method ismClosable
//     * @return closable
//     * Method that check if alert have a button
//     */
//    public boolean ismClosable() {
//        boolean closable = true;
//        try {
//            closable = mClosable;
//        } catch(NullPointerException e) {
//            closable = true;
//        }
//        return closable;
//    }
//
//
//    /**
//     * method getmApps
//     * @return app
//     * Method returns the list of apps in which the alert will appear
//     */
//    public List<String> getmApps() {
//        return mApps;
//    }
//
//
//    /**
//     * method getmContent
//     * @return mContent
//     * Method returns the content alert
//     */
//    public String getmContent() {
//        return mContent;
//    }
//
//
//    /**
//     * method getmFrom
//     * @return from
//     * Method that converts a text string to LocalDateTime and returns it
//     */
//    public LocalDateTime getmFrom() {
//        String fromString = "";
//        LocalDateTime from = null;
//        try {
//            fromString = mFrom;
//            if(!fromString.equals("")) {
//                from = LocalDateTime.parse(String.format(fromString, DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
//            }
//        } catch(NullPointerException e) {
//            fromString = "";
//        }
//
//        return from;
//    }
//
//
//    /**
//     * method getmTo
//     * @return to
//     * Method that converts a text string to LocalDateTime and returns it
//     */
//    public LocalDateTime getmTo() {
//        String toString = "";
//        LocalDateTime to = null;
//        try {
//            toString = mTo;
//            if(!toString.equals("")) {
//                to = LocalDateTime.parse(String.format(toString, DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
//            }
//        } catch(NullPointerException e) {
//            toString = "";
//        }
//
//        return to;
//    }
//
//
//    /**
//     * method getmTitle
//     * @return title
//     * Method that returns the title of the alert
//     */
//    public String getmTitle() {
//        return (mTitle.equals("")) ? "Aviso importante" : mTitle;
//    }
//
//
//    /**
//     * method getmType
//     * @return type
//     * Method returns the type alert
//     */
//    public String getmType() {
//        String type = "default";
//        try {
//            type = (mType.equals("") ? "default" : mType);
//        } catch(NullPointerException e) {
//            type = "default";
//        }
//        return type;
//    }
//
//}
//
