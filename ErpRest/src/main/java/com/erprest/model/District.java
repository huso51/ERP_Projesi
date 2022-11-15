/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

import com.erprest.dao.CustomerDao;
import java.util.List;

/**
 *
 * @author msi_ge72
 */
public class District {

    private long id;
    private String name;

    private long cityId;  // EKLEDİM
    private City city;    // EKLEDİM

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

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public static District byCode(City city, String code) {
        List<District> districts = new CustomerDao().getDistricts(city.getId());
        for (District district : districts) {
            if (district.getName().equals(code.toUpperCase())) {
                return district;
            }
        }
        return null;
    }

}
