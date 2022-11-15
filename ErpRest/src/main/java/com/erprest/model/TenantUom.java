
package com.erprest.model;


public class TenantUom {
    
    private long id;
    private String date; 
    
    private String tenantId;
    private Tenant tenant;
    
    private String uomCodes;
    private UomCode uomCode;

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

    public String getUomCodes() {
        return uomCodes;
    }

    public void setUomCodes(String uomCodes) {
        this.uomCodes = uomCodes;
    }

    public UomCode getUomCode() {
        return uomCode;
    }

    public void setUomCode(UomCode uomCode) {
        this.uomCode = uomCode;
    }
    
    
    
}
