package com.mobiquel.udhampur.pojo.apirequest;

public class ProdIdUserIdRequest {
    private String userId;
    private String prodId;
    private String isLoggedIn;

    public ProdIdUserIdRequest() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(String isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
}
