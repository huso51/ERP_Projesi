/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

import java.sql.Date;
import java.util.List;

/**
 *
 * @author msi_ge72
 */
public class Customer {
    
    private long id;
    private String name;
    private String surname;
    private String appellation;
    private String fullAppellation;
    private long customerTypeId;
    private String taxAdministration;
    private String tc;
    private String tradeRegisterNumber;
    private String createdAt;
    private String confirmedAt;
    private String mersisNumber;
    private long basicDiscount;
    private long creditLimit;
    private boolean isAssent;
    private String tenantId;
    private CustomerType customerType;
    private long defaultAddressId;
    private List<Address> address;
    private boolean isEfaturaUser;
    private String senderIdentifier;
    private Double remainder;

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }


    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String Surname) {
        this.surname = Surname;
    }

    public long getCustomerTypeId() {
        return customerTypeId;
    }

    public void setCustomerTypeId(long customerTypeId) {
        this.customerTypeId = customerTypeId;
    }

    public String getTaxAdministration() {
        return taxAdministration;
    }

    public void setTaxAdministration(String taxAdministration) {
        this.taxAdministration = taxAdministration;
    }

    public String getMersisNumber() {
        return mersisNumber;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isIsAssent() {
        return isAssent;
    }

    public void setIsAssent(boolean isAssent) {
        this.isAssent = isAssent;
    }

    public void setMersisNumber(String MersisNumber) {
        this.mersisNumber = MersisNumber;
    }

    public long getBasicDiscount() {
        return basicDiscount;
    }

    public void setBasicDiscount(long basicDiscount) {
        this.basicDiscount = basicDiscount;
    }

    public long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(long creditLimit) {
        this.creditLimit = creditLimit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(String confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTradeRegisterNumber() {
        return tradeRegisterNumber;
    }

    public void setTradeRegisterNumber(String tradeRegisterNumber) {
        this.tradeRegisterNumber = tradeRegisterNumber;
    }


    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public String getFullAppellation() {
        return fullAppellation;
    }

    public void setFullAppellation(String fullAppellation) {
        this.fullAppellation = fullAppellation;
    }

    public long getDefaultAddressId() {
        return defaultAddressId;
    }

    public void setDefaultAddressId(long defaultAddressId) {
        this.defaultAddressId = defaultAddressId;
    }

    public boolean isIsEfaturaUser() {
        return isEfaturaUser;
    }

    public void setIsEfaturaUser(boolean isEfaturaUser) {
        this.isEfaturaUser = isEfaturaUser;
    }

    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    public void setSenderIdentifier(String senderIdentifier) {
        this.senderIdentifier = senderIdentifier;
    }

    public Double getRemainder() {
        return remainder;
    }

    public void setRemainder(Double remainder) {
        this.remainder = remainder;
    }
    
}
