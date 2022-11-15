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
public class EInvoiceStatistic {

    private long adminCount;
    private long userCount;
    private long incomingCount;
    private long outgoingCount;
    private long remainingCredit;
    private long spentCredit;
    private long totalCredit;
    private long creditRequestCount;

    public long getAdminCount() {
        return adminCount;
    }

    public void setAdminCount(long adminCount) {
        this.adminCount = adminCount;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getIncomingCount() {
        return incomingCount;
    }

    public void setIncomingCount(long incomingCount) {
        this.incomingCount = incomingCount;
    }

    public long getOutgoingCount() {
        return outgoingCount;
    }

    public void setOutgoingCount(long outgoingCount) {
        this.outgoingCount = outgoingCount;
    }

    public long getRemainingCredit() {
        return remainingCredit;
    }

    public void setRemainingCredit(long remainingCredit) {
        this.remainingCredit = remainingCredit;
    }

    public long getSpentCredit() {
        return spentCredit;
    }

    public void setSpentCredit(long spentCredit) {
        this.spentCredit = spentCredit;
    }

    public long getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(long totalCredit) {
        this.totalCredit = totalCredit;
    }

    public long getCreditRequestCount() {
        return creditRequestCount;
    }

    public void setCreditRequestCount(long creditRequestCount) {
        this.creditRequestCount = creditRequestCount;
    }

}
