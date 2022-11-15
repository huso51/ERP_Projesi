/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import com.erprest.dao.EInvoiceDao;
import com.erprest.filter.CustomException;
import com.erprest.model.Integration;
import com.erprest.model.ResponseData;
import com.erprest.model.User;
import com.google.gson.Gson;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author msi_ge72
 */
@Path("einvoice")
public class EInvoiceResource {

    Gson gson = new Gson();

    @POST
    @Path("registerAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerAdmin(
            @FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("einvoiceUsername") String einvoiceUsername,
            @FormParam("einvoicePassword") String einvoicePassword) throws CustomException {
        ResponseData<Response> response = new ResponseData();
        new EInvoiceDao().registerAdmin(name, email, einvoiceUsername, einvoicePassword);
        response.succeed("e-Fatura admin ekleme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("registerMukellef")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerMukellef(
            @FormParam("adminUsername") String adminUsername,
            @FormParam("adminPassword") String adminPassword,
            @FormParam("integration") String integrationJson) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        Integration integration = gson.fromJson(integrationJson, Integration.class);
        EInvoiceDao eInvoiceDao = new EInvoiceDao();
        User authAdmin = eInvoiceDao.getConnectedAdmin(adminUsername, adminPassword);
        if (authAdmin == null) {
            response.setError(401, "Yetkisiz giriş");
        } else {
            new EInvoiceDao().registerMukellef(authAdmin, integration);
            response.succeed("e-Fatura kullanıcı ekleme tamamlandı.");
        }
        return response.finalResponse();
    }

}
