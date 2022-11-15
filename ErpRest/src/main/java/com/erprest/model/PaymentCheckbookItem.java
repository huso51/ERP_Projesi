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
public class PaymentCheckbookItem {

    private long id;
    private PaymentCheckbook PaymentCheckbook;
    private double amount;
    private boolean isGivenToCustomer;
    private boolean isPaid;
    private TenantAccount tenantAccount;
    private String checkbookDate;
    private String paymentDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PaymentCheckbook getPaymentCheckbook() {
        return PaymentCheckbook;
    }

    public void setPaymentCheckbook(PaymentCheckbook PaymentCheckbook) {
        this.PaymentCheckbook = PaymentCheckbook;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isIsGivenToCustomer() {
        return isGivenToCustomer;
    }

    public void setIsGivenToCustomer(boolean isGivenToCustomer) {
        this.isGivenToCustomer = isGivenToCustomer;
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

    public String getCheckbookDate() {
        return checkbookDate;
    }

    public void setCheckbookDate(String checkbookDate) {
        this.checkbookDate = checkbookDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

}
