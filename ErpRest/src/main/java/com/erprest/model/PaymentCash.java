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
public class PaymentCash {

    private long id;
    private String name;
    private String description;
    private boolean isPayingToCustomer;
    private TenantAccount tenantAccount;
    private double amount;
    private String createdAt;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsPayingToCustomer() {
        return isPayingToCustomer;
    }

    public void setIsPayingToCustomer(boolean isPayingToCustomer) {
        this.isPayingToCustomer = isPayingToCustomer;
    }

    public TenantAccount getTenantAccount() {
        return tenantAccount;
    }

    public void setTenantAccount(TenantAccount tenantAccount) {
        this.tenantAccount = tenantAccount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
