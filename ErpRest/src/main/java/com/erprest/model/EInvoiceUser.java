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
public class EInvoiceUser {
    private long id;
    private EInvoiceUser admin;
    private long ownerId;
    private String email;
    private String password;
    private String vkn;
    private String name;
    private String gb;
    private String pk;
    private String authorization;
    private long credit;
    private boolean isUser;
    private boolean isAdmin;
    private boolean isSuperAdmin;
    private String code;
    private boolean confirmed;
    private String confirmedString;
    private String confirmedAt;
    private String createdAt;
    private EInvoiceStatistic statistic;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EInvoiceUser getAdmin() {
        return admin;
    }

    public void setAdmin(EInvoiceUser admin) {
        this.admin = admin;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVkn() {
        return vkn;
    }

    public void setVkn(String vkn) {
        this.vkn = vkn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGb() {
        return gb;
    }

    public void setGb(String gb) {
        this.gb = gb;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public long getCredit() {
        return credit;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    public boolean isIsUser() {
        return isUser;
    }

    public void setIsUser(boolean isUser) {
        this.isUser = isUser;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getConfirmedString() {
        return confirmedString;
    }

    public void setConfirmedString(String confirmedString) {
        this.confirmedString = confirmedString;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(String confirmedAt) {
        if (confirmedAt != null ) {
            this.confirmedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(confirmedAt);
        } else {
            this.confirmedAt = null;
        }
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        if (createdAt != null ) {
            this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        } else {
            this.createdAt = null;
        }
    }

    public EInvoiceStatistic getStatistic() {
        return statistic;
    }

    public void setStatistic(EInvoiceStatistic statistic) {
        this.statistic = statistic;
    }

}
