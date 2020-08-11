package com.mobiquel.udhampur.pojo;

/**
 * Created by landshark on 19/9/17.
 */

public class LanguageModel {

    private String title,title2;

    public LanguageModel() {
    }

    public LanguageModel(String title,String title2) {
        this.title = title;
        this.title2 = title2;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

}
