/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

import com.erprest.dao.InvoiceDao;
import java.util.List;

/**
 *
 * @author msi_ge72
 */
public class InvoiceType {

    private long id;
    private String code;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static InvoiceType byCode(String code) {
        List<InvoiceType> types = new InvoiceDao().getInvoiceTypes();
        for (InvoiceType type : types) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
