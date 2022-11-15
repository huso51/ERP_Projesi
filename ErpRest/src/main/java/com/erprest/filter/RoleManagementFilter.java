/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.filter;

import com.erprest.model.User;
import com.erprest.model.UserRole;
import java.io.IOException;
import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author msi_ge72
 */
@Priority(2)
@Provider
public class RoleManagementFilter implements ContainerRequestFilter {

    @Context
    private transient HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        UriInfo info = requestContext.getUriInfo();
        String requestedUri = info.getPath();
        if (!(info.getPath().contains("auth")
                || info.getPath().contains("einvoice")
                || info.getPath().contains("integrate")
                || info.getPath().contains("confirmEinvoiceAdmin"))) {

            User authUser = (User) this.servletRequest.getAttribute("authUser");
            UserRole role = authUser.getTenantUsers().getUserRole();

            if (requestedUri.contains("sessions/users")) {

                if (!(requestedUri.contains("get") && role.getUser().matches("a|r")
                        || requestedUri.contains("add") && role.getUser().matches("a|w")
                        || requestedUri.contains("update") && role.getUser().matches("a|w")
                        || requestedUri.contains("delete") && role.getUser().matches("a|w")
                        || requestedUri.contains("updateDefaultTenant")
                        || requestedUri.contains("getGibUsers"))) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }

            } else if (requestedUri.contains("sessions/tenants")) {
                if (!(requestedUri.contains("get") && role.getTenant().matches("a|r")
                        || requestedUri.contains("add") && role.getTenant().matches("a|w")
                        || requestedUri.contains("delete") && role.getTenant().matches("a|w")
                        || requestedUri.contains("expireRequest") && role.isIsTenantAdmin()
                        || requestedUri.contains("email") && role.isIsTenantAdmin()
                        || (requestedUri.contains("showExpireRequests") || requestedUri.contains("acceptExpireRequest")) && role.isIsTenantAdmin() && authUser.getTenantUsers().getTenant().getTenantType().equals("SUPERADMIN")
                        || requestedUri.contains("update") && role.getTenant().matches("a|w"))) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }

            } else if (requestedUri.contains("sessions/customers")) {

                if (!(requestedUri.contains("get") && role.getCustomer().matches("a|r")
                        || requestedUri.contains("add") && role.getCustomer().matches("a|w")
                        || requestedUri.contains("delete") && role.getCustomer().matches("a|w")
                        || requestedUri.contains("update") && role.getCustomer().matches("a|w"))) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }

            } else if (requestedUri.contains("sessions/items")) {

                if (!(requestedUri.contains("get") && role.getItem().matches("a|r")
                        || requestedUri.contains("add") && role.getItem().matches("a|w")
                        || requestedUri.contains("update") && role.getItem().matches("a|w")
                        || requestedUri.contains("delete") && role.getItem().matches("a|w"))) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }

            } else if (requestedUri.contains("sessions/invoices")) {

                if (!(requestedUri.contains("get") && role.getInvoice().matches("a|r")
                        || requestedUri.contains("add") && role.getInvoice().matches("a|w")
                        || requestedUri.contains("update") && role.getInvoice().matches("a|w")
                        || requestedUri.contains("delete") && role.getInvoice().matches("a|w")
                        || requestedUri.contains("send") && role.getInvoice().matches("a|w|r"))) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }
            } else if (requestedUri.contains("sessions/gib")) {

            } else if (requestedUri.contains("auth")) {

            } else if (requestedUri.contains("einvoice")) {

            } else if (requestedUri.contains("account")) {

                if (!(requestedUri.contains("get") && role.getAccount().matches("a|r")
                        || requestedUri.contains("add") && role.getAccount().matches("a|w")
                        || requestedUri.contains("update") && role.getAccount().matches("a|w")
                        || requestedUri.contains("delete") && role.getAccount().matches("a|w")
                        || requestedUri.contains("pay") && role.getAccount().matches("a|w"))) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }
            } else {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }

        }
    }

}
