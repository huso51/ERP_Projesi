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
public class Dashboard {
    
    private long inboxCount;
    private long outboxCount;
    private long customerCount;
    private List<Item> item;
    private long itemCount;
    private long remainingExpireDate;
    private List<Invoice> inbox;
    private List<Invoice> outbox;
    private EInvoiceStatistic statistic;

    public long getInboxCount() {
        return inboxCount;
    }

    public void setInboxCount(long inboxCount) {
        this.inboxCount = inboxCount;
    }

    public long getOutboxCount() {
        return outboxCount;
    }

    public void setOutboxCount(long outboxCount) {
        this.outboxCount = outboxCount;
    }

    public long getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(long customerCount) {
        this.customerCount = customerCount;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }

    public long getItemCount() {
        return itemCount;
    }

    public void setItemCount(long itemCount) {
        this.itemCount = itemCount;
    }

    public long getRemainingExpireDate() {
        return remainingExpireDate;
    }

    public void setRemainingExpireDate(long remainingExpireDate) {
        this.remainingExpireDate = remainingExpireDate;
    }

    public List<Invoice> getInbox() {
        return inbox;
    }

    public void setInbox(List<Invoice> inbox) {
        this.inbox = inbox;
    }

    public List<Invoice> getOutbox() {
        return outbox;
    }

    public void setOutbox(List<Invoice> outbox) {
        this.outbox = outbox;
    }

    public EInvoiceStatistic getStatistic() {
        return statistic;
    }

    public void setStatistic(EInvoiceStatistic statistic) {
        this.statistic = statistic;
    }

    
}
