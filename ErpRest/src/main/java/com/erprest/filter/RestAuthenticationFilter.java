/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.filter;

import com.erprest.dao.AuthDao;
import com.erprest.dao.UserDao;
import com.erprest.model.User;
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
@Priority(1)
@Provider
public class RestAuthenticationFilter implements ContainerRequestFilter {

    @Context
    private transient HttpServletRequest servletRequest;

    public RestAuthenticationFilter() {
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        UriInfo info = requestContext.getUriInfo();
        AuthDao authdao = new AuthDao();
        if (!(info.getPath().contains("auth")
                ||info.getPath().contains("einvoice")
                ||info.getPath().contains("integrate")
                ||info.getPath().contains("confirmEinvoiceAdmin"))) {

            String authCredentials = requestContext.getHeaderString("Authorization");

            User loginUser = null;
            
            if (null == authCredentials) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            } else {
                User userCredentials = authdao.parseAuthentication(authCredentials);
                loginUser = authdao.login(userCredentials.getEmail(), userCredentials.getPassword());
            }
            
            if (loginUser == null || loginUser.getId() == null) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            } else {
                UserDao userdao = new UserDao();
                User authUser = userdao.getUserInfo(loginUser.getId());
                authUser.setRememberToken(authdao.parseAuthentication(authCredentials).getRememberToken());

                this.servletRequest.setAttribute("authUser", authUser); //authenticated user info

            }

        }
    }

}
