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
public class InvoiceLine {

    private long id;
    private long invoiceId;
    private long itemId;
    private Item item;
    private double price;
    private double quantity;
    private long discountAmount;
    private double discountTotal;
    private double taxesTotal;
    private double subTotal;
    private double grossTotal;
    private double otvIncludedTotal;
    private List<InvoiceLineTaxes> invoiceLineTaxes;
    private double lastPrice;
    private String exceptionReason;
    private double currencyMultiplier;

    public long getId() {
        return id;
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(double grossTotal) {
        this.grossTotal = grossTotal;
    }


    public double getTaxesTotal() {
        return taxesTotal;
    }

    public void setTaxesTotal(double taxesTotal) {
        this.taxesTotal = taxesTotal;
    }

    public double getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(double discountTotal) {
        this.discountTotal = discountTotal;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<InvoiceLineTaxes> getInvoiceLineTaxes() {
        return invoiceLineTaxes;
    }

    public void setInvoiceLineTaxes(List<InvoiceLineTaxes> incoiceTaxes) {
        this.invoiceLineTaxes = incoiceTaxes;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getOtvIncludedTotal() {
        return otvIncludedTotal;
    }

    public void setOtvIncludedTotal(double otvIncludedTotal) {
        this.otvIncludedTotal = otvIncludedTotal;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    public double getCurrencyMultiplier() {
        return currencyMultiplier;
    }

    public void setCurrencyMultiplier(double currencyMultiplier) {
        this.currencyMultiplier = currencyMultiplier;
    }

   
}
