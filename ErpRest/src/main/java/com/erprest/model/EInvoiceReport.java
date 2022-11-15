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
public class EInvoiceReport {
    private String uuid;
    private String periodUuid;
    private String vknTckn;
    private byte[] content;
    private int partNo;
    private String issueDateTime;
    private String periodStart;
    private String periodEnd;
    private String partStart;
    private String partEnd;
    private float size;
    private boolean isSended;
    private String statusDescription;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPeriodUuid() {
        return periodUuid;
    }

    public void setPeriodUuid(String periodUuid) {
        this.periodUuid = periodUuid;
    }

    public String getVknTckn() {
        return vknTckn;
    }

    public void setVknTckn(String vknTckn) {
        this.vknTckn = vknTckn;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getPartNo() {
        return partNo;
    }

    public void setPartNo(int partNo) {
        this.partNo = partNo;
    }

    public String getIssueDateTime() {
        return issueDateTime;
    }

    public void setIssueDateTime(String issueDateTime) {
        this.issueDateTime = issueDateTime;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getPartStart() {
        return partStart;
    }

    public void setPartStart(String partStart) {
        this.partStart = partStart;
    }

    public String getPartEnd() {
        return partEnd;
    }

    public void setPartEnd(String partEnd) {
        this.partEnd = partEnd;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public boolean isIsSended() {
        return isSended;
    }

    public void setIsSended(boolean isSended) {
        this.isSended = isSended;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
    
}
