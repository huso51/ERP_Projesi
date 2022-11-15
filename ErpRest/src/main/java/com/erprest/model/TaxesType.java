/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

import com.erprest.dao.ItemDao;
import java.util.List;

/**
 *
 * @author msi_ge72
 */
public class TaxesType {

    private long id;
    private String code;
    private double taxAmount;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static TaxesType byCode(String code) {
        List<TaxesType> types = new ItemDao().getTaxesType();
        for (TaxesType type : types) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
