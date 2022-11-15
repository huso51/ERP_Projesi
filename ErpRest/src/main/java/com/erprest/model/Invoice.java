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
public class Invoice {

    private long id;
    private String tenantId;
    private Tenant tenant;
    private String sn1;
    private String sn2;
    private String sn3;
    private long currencyId;
    private Currency currency;
    private String createdAt;
    private String orderNo;
    private String waybillNumber;
    private String uuid;
    private long customerId;
    private Customer customer;
    private List<InvoiceLine> invoiceLine;
    private boolean confirmed;
    private long InvoiceTypeId;
    private InvoiceType invoiceType;
    private long invoiceWithholdingId;
    private long invoiceExceptionId;
    private InvoiceWithholding invoiceWithholding;
    private InvoiceException invoiceException;
    private double subTotal;
    private double discountTotal;
    private double grossTotal;
    private double taxesTotal;
    private double priceTotal;
    private double otvTotal;
    private String orderDate;
    private String waybillDate;
    private List<InvoiceTaxes> invoiceTaxes;
    private boolean isComingInvoice;
    private String receiverIdentifier;
    private long invoiceScenarioId;
    private InvoiceScenario invoiceScenario;
    private String note;
    private SpendingType spendingType;

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<InvoiceLine> getInvoiceLine() {
        return invoiceLine;
    }

    public void setInvoiceLine(List<InvoiceLine> invoiceLine) {
        this.invoiceLine = invoiceLine;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTenant_id() {
        return tenantId;
    }

    public void setTenant_id(String tenant_id) {
        this.tenantId = tenant_id;
    }

    public long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(long currencyId) {
        this.currencyId = currencyId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSn1() {
        return sn1;
    }

    public void setSn1(String sn1) {
        this.sn1 = sn1;
    }

    public String getSn2() {
        return sn2;
    }

    public void setSn2(String sn2) {
        this.sn2 = sn2;
    }

    public String getSn3() {
        return sn3;
    }

    public void setSn3(String sn3) {
        this.sn3 = sn3;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(double discountTotal) {
        this.discountTotal = discountTotal;
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

    public double getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(double priceTotal) {
        this.priceTotal = priceTotal;
    }

    public double getOtvTotal() {
        return otvTotal;
    }

    public void setOtvTotal(double otvTotal) {
        this.otvTotal = otvTotal;
    }

    public long getInvoiceTypeId() {
        return InvoiceTypeId;
    }

    public void setInvoiceTypeId(long InvoiceTypeId) {
        this.InvoiceTypeId = InvoiceTypeId;
    }

    public long getInvoiceWithholdingId() {
        return invoiceWithholdingId;
    }

    public void setInvoiceWithholdingId(long invoiceWithholdingId) {
        this.invoiceWithholdingId = invoiceWithholdingId;
    }

    public InvoiceWithholding getInvoiceWithholding() {
        return invoiceWithholding;
    }

    public void setInvoiceWithholding(InvoiceWithholding invoiceWithholding) {
        this.invoiceWithholding = invoiceWithholding;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public long getInvoiceExceptionId() {
        return invoiceExceptionId;
    }

    public void setInvoiceExceptionId(long invoiceExceptionId) {
        this.invoiceExceptionId = invoiceExceptionId;
    }

    public InvoiceException getInvoiceException() {
        return invoiceException;
    }

    public void setInvoiceException(InvoiceException invoiceException) {
        this.invoiceException = invoiceException;
    }

    public List<InvoiceTaxes> getInvoiceTaxes() {
        return invoiceTaxes;
    }

    public void setInvoiceTaxes(List<InvoiceTaxes> invoiceTaxes) {
        this.invoiceTaxes = invoiceTaxes;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getWaybillDate() {
        return waybillDate;
    }

    public void setWaybillDate(String waybillDate) {
        this.waybillDate = waybillDate;
    }

    public boolean isIsComingInvoice() {
        return isComingInvoice;
    }

    public void setIsComingInvoice(boolean isComingInvoice) {
        this.isComingInvoice = isComingInvoice;
    }

    public String getReceiverIdentifier() {
        return receiverIdentifier;
    }

    public void setReceiverIdentifier(String receiver_identifier) {
        this.receiverIdentifier = receiver_identifier;
    }

    public InvoiceScenario getInvoiceScenario() {
        return invoiceScenario;
    }

    public void setInvoiceScenario(InvoiceScenario invoiceScenario) {
        this.invoiceScenario = invoiceScenario;
    }

    public long getInvoiceScenarioId() {
        return invoiceScenarioId;
    }

    public void setInvoiceScenarioId(long invoiceScenarioId) {
        this.invoiceScenarioId = invoiceScenarioId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public SpendingType getSpendingType() {
        return spendingType;
    }

    public void setSpendingType(SpendingType spendingType) {
        this.spendingType = spendingType;
    }

}
