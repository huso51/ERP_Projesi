/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

/**
 *
 * @author msi_ge72
 */
public class Neighborhood {
    
    private long id;
    private String name;
    private long postCode;
    
    private District district;
    private long districtsId;  // EKLEDİM

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPostCode() {
        return postCode;
    }

    public void setPostCode(long postCode) {
        this.postCode = postCode;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public long getDistrictsId() {
        return districtsId;
    }

    public void setDistrictsId(long districtsId) {
        this.districtsId = districtsId;
    }
    
    
    
}
