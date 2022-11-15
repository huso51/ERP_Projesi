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
public class ItemTaxes {
    
    private long id;
    private long itemId;
    private String code;
    private long value;
    private long taxesTypeId;
    private TaxesType taxesType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTaxesTypeId() {
        return taxesTypeId;
    }

    public void setTaxesTypeId(long taxesTypeId) {
        this.taxesTypeId = taxesTypeId;
    }

    public TaxesType getTaxesType() {
        return taxesType;
    }

    public void setTaxesType(TaxesType taxesType) {
        this.taxesType = taxesType;
    }


    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }


}
