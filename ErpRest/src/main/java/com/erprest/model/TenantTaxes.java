
package com.erprest.model;


public class TenantTaxes {
    
    private long id;     
    private String date;    
    
    private String tenantId;     
    private Tenant tenant;     
    
    private long taxesTypeId;    
    private TaxesType taxesType;     

    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public long getTaxesTypeId() {
        return taxesTypeId;
    }

    public void setTaxesTypeId(long taxesTypeId) {
        this.taxesTypeId = taxesTypeId;
    }

    public TaxesType getTaxesType() {
        return taxesType;
    }

    public void setTaxesType(TaxesType taxesType) {
        this.taxesType = taxesType;
    }

    
    
            
}
