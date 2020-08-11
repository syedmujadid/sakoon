package com.mobiquel.udhampur.pojo;

public class CollegeListModel {

    private String name;
    private String collegeId;

    public String getName() {
        return name;
    }

    public CollegeListModel(String name, String collegeId) {
        this.name = name;
        this.collegeId = collegeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }
}
