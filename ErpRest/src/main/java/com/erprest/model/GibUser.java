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
public class GibUser {

    private String identifier;
    private String alias;
    private String userType;
    private String title;
    private String firstCreationTime;
    private String aliasCreationTime;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstCreationTime() {
        return firstCreationTime;
    }

    public void setFirstCreationTime(String firstCreationTime) {
        this.firstCreationTime = firstCreationTime;
    }

    public String getAliasCreationTime() {
        return aliasCreationTime;
    }

    public void setAliasCreationTime(String aliasCreationTime) {
        this.aliasCreationTime = aliasCreationTime;
    }

}
