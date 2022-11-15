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
public class Item {
    
    private long id;
    private String tenantId;
    private Tenant tenant;
    private String name;
    private String description;
    private long uomId;
    private UomCode uomCode;
    private Category category;
    private Storage storage;
    private long categoryId;
    private double stock;
    private double price;
    private long currencyId;
    private Currency currency;
    private String code;
    private List<ItemTaxes> itemTaxes;
    private String created_at;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getUomId() {
        return uomId;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void setUomId(long uomId) {
        this.uomId = uomId;
    }

    public UomCode getUomCode() {
        return uomCode;
    }

    public void setUomCode(UomCode uomCode) {
        this.uomCode = uomCode;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(long currencyId) {
        this.currencyId = currencyId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ItemTaxes> getItemTaxes() {
        return itemTaxes;
    }

    public void setItemTaxes(List<ItemTaxes> itemTaxes) {
        this.itemTaxes = itemTaxes;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
