package com.mobiquel.udhampur.pojo;

public class DamageDetailModel {

    private String propertyType = "", damageDetail = "", quantity = "", baseAmnt = "", totalAmnt = "",category,lat = "",lon = "";

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getDamageDetail() {
        return damageDetail;
    }

    public void setDamageDetail(String damageDetail) {
        this.damageDetail = damageDetail;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBaseAmnt() {
        return baseAmnt;
    }

    public void setBaseAmnt(String baseAmnt) {
        this.baseAmnt = baseAmnt;
    }

    public String getTotalAmnt() {
        return totalAmnt;
    }

    public void setTotalAmnt(String totalAmnt) {
        this.totalAmnt = totalAmnt;
    }
}
