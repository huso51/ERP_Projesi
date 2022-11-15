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
public class GibInbox {
    
    private String uuid;
    private String invoiceId;
    private String profile;
    private String typeCode;
    private String issueDate;
    private String issueTime;
    private String envelopeId;
    private String envelopeDate;
    private String date;
    private String senderPartyId;
    private String senderPartyName;
    private String senderPartyAlias;
    private String receiverPartyId;
    private String receiverPartyAlias;
    private String receiverPartyName;
    private String taxSchemeName;
    private String payableAmount;
    private String currency;
    private String statusCode;
    private String statusDescription;
    private String applicationResponseId;
    private String applicationResponse;
    private String applicationResponseDate;
    private String applicationResponseDescription;
    private String isCancelled;
    private String cancelDate;
    private String cancelReason;
    private Boolean isReaded;
    private Boolean isProcessed;
    private Boolean isViewed;
    private Boolean isReported;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getEnvelopeId() {
        return envelopeId;
    }

    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }

    public String getEnvelopeDate() {
        return envelopeDate;
    }

    public void setEnvelopeDate(String envelopeDate) {
        this.envelopeDate = envelopeDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTaxSchemeName() {
        return taxSchemeName;
    }

    public void setTaxSchemeName(String taxSchemeName) {
        this.taxSchemeName = taxSchemeName;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getApplicationResponseId() {
        return applicationResponseId;
    }

    public void setApplicationResponseId(String applicationResponseId) {
        this.applicationResponseId = applicationResponseId;
    }

    public String getApplicationResponse() {
        return applicationResponse;
    }

    public void setApplicationResponse(String applicationResponse) {
        this.applicationResponse = applicationResponse;
    }

    public String getApplicationResponseDate() {
        return applicationResponseDate;
    }

    public void setApplicationResponseDate(String applicationResponseDate) {
        this.applicationResponseDate = applicationResponseDate;
    }

    public String getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(String isCancelled) {
        this.isCancelled = isCancelled;
    }

    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getSenderPartyId() {
        return senderPartyId;
    }

    public void setSenderPartyId(String senderPartyId) {
        this.senderPartyId = senderPartyId;
    }

    public String getSenderPartyAlias() {
        return senderPartyAlias;
    }

    public void setSenderPartyAlias(String senderPartyAlias) {
        this.senderPartyAlias = senderPartyAlias;
    }

    public String getReceiverPartyId() {
        return receiverPartyId;
    }

    public void setReceiverPartyId(String receiverPartyId) {
        this.receiverPartyId = receiverPartyId;
    }

    public String getReceiverPartyAlias() {
        return receiverPartyAlias;
    }

    public void setReceiverPartyAlias(String receiverPartyAlias) {
        this.receiverPartyAlias = receiverPartyAlias;
    }

    public String getApplicationResponseDescription() {
        return applicationResponseDescription;
    }

    public void setApplicationResponseDescription(String applicationResponseDescription) {
        this.applicationResponseDescription = applicationResponseDescription;
    }

    public Boolean getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(Boolean isReaded) {
        this.isReaded = isReaded;
    }

    public Boolean getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    public Boolean getIsViewed() {
        return isViewed;
    }

    public void setIsViewed(Boolean isViewed) {
        this.isViewed = isViewed;
    }

    public Boolean getIsReported() {
        return isReported;
    }

    public void setIsReported(Boolean isReported) {
        this.isReported = isReported;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getSenderPartyName() {
        return senderPartyName;
    }

    public void setSenderPartyName(String senderPartyName) {
        this.senderPartyName = senderPartyName;
    }

    public String getReceiverPartyName() {
        return receiverPartyName;
    }

    public void setReceiverPartyName(String receiverPartyName) {
        this.receiverPartyName = receiverPartyName;
    }

}
