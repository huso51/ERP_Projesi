/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author msi_ge72
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application{
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.erprest.api.AccountResource.class);
        resources.add(com.erprest.api.AuthenticationResource.class);
        resources.add(com.erprest.api.CustomerResource.class);
        resources.add(com.erprest.api.EInvoiceResource.class);
        resources.add(com.erprest.api.GibResource.class);
        resources.add(com.erprest.api.InvoiceResource.class);
        resources.add(com.erprest.api.ItemResource.class);
        resources.add(com.erprest.api.TenantResource.class);
        resources.add(com.erprest.api.UserResource.class);
        resources.add(com.erprest.filter.CrossDomainFilter.class);
        resources.add(com.erprest.filter.ExceptionFilter.class);
        resources.add(com.erprest.filter.RestAuthenticationFilter.class); //Ekledim
        resources.add(com.erprest.filter.RoleManagementFilter.class);
    }
    
}
