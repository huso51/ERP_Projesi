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
public class InvoiceScenario {

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

    public static InvoiceScenario byCode(String code) {
        List<InvoiceScenario> scenarios = new InvoiceDao().getInvoiceScenario();
        for (InvoiceScenario scenario : scenarios) {
            if (scenario.getCode().equals(code)) {
                return scenario;
            }
        }
        return null;
    }

}
