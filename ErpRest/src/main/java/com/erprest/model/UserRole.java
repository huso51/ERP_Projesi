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
public class UserRole {
    private long id;
    private String user;
    private String customer;
    private String tenant;
    private String item;
    private String invoice;
    private String account;
    private boolean isTenantAdmin;
    private boolean isSuperAdmin;
    private boolean isEinvoiceAdmin;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isIsTenantAdmin() {
        return isTenantAdmin;
    }

    public void setIsTenantAdmin(boolean isTenantAdmin) {
        this.isTenantAdmin = isTenantAdmin;
    }

    public boolean isIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public boolean isIsEinvoiceAdmin() {
        return isEinvoiceAdmin;
    }

    public void setIsEinvoiceAdmin(boolean isEinvoiceAdmin) {
        this.isEinvoiceAdmin = isEinvoiceAdmin;
    }

    
}
