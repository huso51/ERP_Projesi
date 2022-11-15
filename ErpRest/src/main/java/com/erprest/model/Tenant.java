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
public class Tenant {

    private String id;
    private String owner;
    private String name;
    private String description;
    private String status;
    private String tenantType;
    private String created_at;
    private Tenant ownerTenant;
    private long tenantInfoId;
    private Customer tenantInfo;
    private String email;
    private List<TenantPrefix> tenantPrefix;  //Çoğa doğru
    private String logo;
    private String signature;
    private String einvoiceUsername;
    private String einvoicePassword;
    private String expireDate;
    private long expireCount;
    private EInvoiceUser user;
    private Dashboard dashboard;
    private String invoiceNote;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenantType() {
        return tenantType;
    }

    public void setTenantType(String tenantType) {
        this.tenantType = tenantType;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Tenant getOwnerTenant() {
        return ownerTenant;
    }

    public void setOwnerTenant(Tenant ownerTenant) {
        this.ownerTenant = ownerTenant;
    }

    public Customer getTenantInfo() {
        return tenantInfo;
    }

    public void setTenantInfo(Customer tenantInfo) {
        this.tenantInfo = tenantInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTenantInfoId() {
        return tenantInfoId;
    }

    public void setTenantInfoId(long tenantInfoId) {
        this.tenantInfoId = tenantInfoId;
    }

    public List<TenantPrefix> getTenantPrefix() {
        return tenantPrefix;
    }

    public void setTenantPrefix(List<TenantPrefix> tenantPrefix) {
        this.tenantPrefix = tenantPrefix;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public long getExpireCount() {
        return expireCount;
    }

    public void setExpireCount(long expireCount) {
        this.expireCount = expireCount;
    }

    public String getEinvoiceUsername() {
        return einvoiceUsername;
    }

    public void setEinvoiceUsername(String einvoiceUsername) {
        this.einvoiceUsername = einvoiceUsername;
    }

    public String getEinvoicePassword() {
        return einvoicePassword;
    }

    public void setEinvoicePassword(String einvoicePassword) {
        this.einvoicePassword = einvoicePassword;
    }

    public EInvoiceUser getUser() {
        return user;
    }

    public void setUser(EInvoiceUser user) {
        this.user = user;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public String getInvoiceNote() {
        return invoiceNote;
    }

    public void setInvoiceNote(String invoiceNote) {
        this.invoiceNote = invoiceNote;
    }

}
