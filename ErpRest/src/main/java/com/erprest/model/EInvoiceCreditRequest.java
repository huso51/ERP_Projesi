/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author msi_ge72
 */
public class EInvoiceCreditRequest {
    private long id;
    private EInvoiceUser user;
    private long amount;
    private String description;
    private boolean confirmed;
    private String confirmedAt;
    private String createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EInvoiceUser getUser() {
        return user;
    }

    public void setUser(EInvoiceUser user) {
        this.user = user;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Date confirmedAt) {
        if (confirmedAt != null) {
            this.confirmedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(confirmedAt);
        } else {
            this.confirmedAt = null;
        }
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        if (createdAt != null) {
            this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        } else {
            this.createdAt = null;
        }
    }
    
    
}
