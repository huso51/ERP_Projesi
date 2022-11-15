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
public class PaymentBill {

    private long id;
    private String name;
    private String description;
    private double amount;
    private Date billDate;
    private String createdAt;
    private List<PaymentBillInstallment> billInstallments;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<PaymentBillInstallment> getBillInstallments() {
        return billInstallments;
    }

    public void setBillInstallments(List<PaymentBillInstallment> billInstallments) {
        this.billInstallments = billInstallments;
    }

}
