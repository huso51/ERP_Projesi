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
public class CustomerPayment {

    private long id;
    private Tenant tenant;
    private Customer customer;
    private String description;
    private String type;
    private PaymentCash paymentCash;
    private PaymentCheckbook paymentCheckbook;
    private PaymentBill paymentBill;
    private double debt;
    private double receivable;
    private double remaining;
    private boolean isPaid;
    private String createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PaymentCash getPaymentCash() {
        return paymentCash;
    }

    public void setPaymentCash(PaymentCash paymentCash) {
        this.paymentCash = paymentCash;
    }

    public PaymentCheckbook getPaymentCheckbook() {
        return paymentCheckbook;
    }

    public void setPaymentCheckbook(PaymentCheckbook paymentCheckbook) {
        this.paymentCheckbook = paymentCheckbook;
    }

    public PaymentBill getPaymentBill() {
        return paymentBill;
    }

    public void setPaymentBill(PaymentBill paymentBill) {
        this.paymentBill = paymentBill;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    public double getReceivable() {
        return receivable;
    }

    public void setReceivable(double receivable) {
        this.receivable = receivable;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public boolean isIsPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
