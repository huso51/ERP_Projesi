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
public class City {

    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long cityId) {
        this.id = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static City byCode(String code) {
        List<City> cities = new CustomerDao().getCities();
        for (City city : cities) {
            if (city.getName().equals(code.toUpperCase())) {
                return city;
            }
        }
        return null;
    }
}
