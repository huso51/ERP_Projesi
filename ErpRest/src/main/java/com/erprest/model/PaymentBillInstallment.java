/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

import java.sql.Date;


/**
 *
 * @author msi_ge72
 */
public class PaymentBillInstallment {

    private long id;
    private PaymentBill paymentBill;
    private double amount;
    private boolean isPaid;
    private TenantAccount tenantAccount;
    private Date expiryDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PaymentBill getPaymentBill() {
        return paymentBill;
    }

    public void setPaymentBill(PaymentBill paymentBill) {
        this.paymentBill = paymentBill;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isIsPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public TenantAccount getTenantAccount() {
        return tenantAccount;
    }

    public void setTenantAccount(TenantAccount tenantAccount) {
        this.tenantAccount = tenantAccount;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

}
