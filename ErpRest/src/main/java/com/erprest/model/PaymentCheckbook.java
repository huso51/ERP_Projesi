/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

import java.util.List;

/**
 *
 * @author msi_ge72
 */
public class PaymentCheckbook {

    private long id;
    private String description;
    private List<PaymentCheckbookItem> checkbookItems;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PaymentCheckbookItem> getCheckbookItems() {
        return checkbookItems;
    }

    public void setCheckbookItems(List<PaymentCheckbookItem> checkbookItems) {
        this.checkbookItems = checkbookItems;
    }

}
