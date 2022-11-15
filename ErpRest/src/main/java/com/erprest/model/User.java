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
public class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private String description;
    private String secretKey;
    private String status;
    private String metadata;
    private long defaultTenantUserId;
    private TenantUsers tenantUsers;
    private String created_at; 
    private String rememberToken;
    private String confirmationCode;
    
    
    private String picture; 
    
    private boolean emailPostId;
    

    public TenantUsers getTenantUsers() {
        return tenantUsers;
    }

    public void setTenantUsers(TenantUsers tenantUsers) {
        this.tenantUsers = tenantUsers;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getDefaultTenantUserId() {
        return defaultTenantUserId;
    }

    public void setDefaultTenantUserId(long defaultTenantUserId) {
        this.defaultTenantUserId = defaultTenantUserId;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isEmailPostId() {
        return emailPostId;
    }

    public void setEmailPostId(boolean emailPostId) {
        this.emailPostId = emailPostId;
    }
    
}
