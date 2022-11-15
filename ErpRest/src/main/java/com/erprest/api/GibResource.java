/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import com.erprest.dao.GibDao;
import com.erprest.dao.TenantDao;
import com.erprest.filter.CustomException;
import com.erprest.model.Dashboard;
import com.erprest.model.EInvoiceCreditRequest;
import com.erprest.model.EInvoiceReport;
import com.erprest.model.EInvoiceUser;
import com.erprest.model.GibInbox;
import com.erprest.model.Integration;
import com.erprest.model.Invoice;
import com.erprest.model.ResponseData;
import com.erprest.model.Tenant;
import com.erprest.model.User;
import com.erprest.service.XmlToInvoice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author msi_ge72
 */
@Path("sessions/gib")
public class GibResource {

    @Context
    private transient HttpServletRequest servletRequest;

    Gson gson = new Gson();

    @POST
    @Path("/sendInvoice")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendInvoice(
            @FormParam("invoices") String invoiceJson) throws CustomException {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        List<Invoice> invoices = gson.fromJson(invoiceJson, new TypeToken<List<Invoice>>() {
        }.getType());
        GibDao gibdao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibdao.sendInvoice(invoices);
        return response.finalResponse();
    }

    @POST
    @Path("/getInbox")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInbox(
            @FormParam("id") String id,
            @FormParam("senderVkn") String senderVkn,
            @FormParam("profile") String profile,
            @FormParam("startDate") String startDate,
            @FormParam("endDate") String endDate,
            @FormParam("limit") long limit,
            @FormParam("offset") long offset) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<List<GibInbox>> response = new ResponseData<>();
        GibDao gibdao = new GibDao(authUser.getTenantUsers().getTenant());
        List<GibInbox> gibInbox = gibdao.getInvoices(false, id, senderVkn, profile, startDate, endDate, limit, offset);
        response.setData(gibInbox);
        return response.finalResponse();
    }

    @POST
    @Path("/getOutbox")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutbox(
            @FormParam("id") String id,
            @FormParam("receiverVkn") String receiverVkn,
            @FormParam("profile") String profile,
            @FormParam("startDate") String startDate,
            @FormParam("endDate") String endDate,
            @FormParam("limit") long limit,
            @FormParam("offset") long offset) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<List<GibInbox>> response = new ResponseData<>();
        GibDao gibdao = new GibDao(authUser.getTenantUsers().getTenant());
        List<GibInbox> gibInbox = gibdao.getInvoices(true, id, receiverVkn, profile, startDate, endDate, limit, offset);
        response.setData(gibInbox);
        return response.finalResponse();
    }

    @POST
    @Path("/performAction")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptInvoice(
            @FormParam("uuid") String uuid,
            @FormParam("action") String action,
            @FormParam("description") String description) throws CustomException {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibdao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibdao.actionInvoice(uuid, action, description);
        return response.finalResponse();
    }

    @POST
    @Path("/getInvoiceFile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutboxXml(
            @FormParam("uuid") String uuid,
            @FormParam("isOutbox") boolean isOutbox,
            @FormParam("fileType") String fileType) throws CustomException {
        ResponseData<HashMap<String, byte[]>> response = new ResponseData<>();
        if (!(fileType.equals("XML") || fileType.equals("PDF") || fileType.equals("HTML")
                || fileType.equals("EXCEL") || fileType.equals("ARCHIVE"))) {
            response.setError(406, "fileType XML,PDF,HTML,EXCEL,ARCHIVE değerlerinden birini alabilir.");
        } else {
            User authUser = (User) this.servletRequest.getAttribute("authUser");
            GibDao gibdao = new GibDao(authUser.getTenantUsers().getTenant());
            HashMap<String, byte[]> hmap = gibdao.getInvoiceFile(isOutbox, fileType, uuid);
            Map.Entry<String, byte[]> mentry = hmap.entrySet().iterator().next();
            if (fileType.equals("HTML")) {
                ResponseData<String> htmlResponse = new ResponseData<>();
                htmlResponse.setData(new String(mentry.getValue(), StandardCharsets.UTF_8));
                return htmlResponse.finalResponse();
            } else {
                response.setData(hmap);
            }
        }
        return response.finalResponse();
    }

    @POST
    @Path("/getInboxJson")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInboxJson(
            @FormParam("uuid") String uuid) throws CustomException {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<Invoice> response = new ResponseData<>();
        XmlToInvoice xmlToInvoice = new XmlToInvoice();
        GibDao gibdao = new GibDao(authUser.getTenantUsers().getTenant());
        HashMap<String, byte[]> hmap = gibdao.getInvoiceFile(false, "XML", uuid);
        Map.Entry<String, byte[]> mentry = hmap.entrySet().iterator().next();
        response.setData(
                xmlToInvoice.getInvoice(
                        authUser.getTenantUsers().getTenant(), new String(mentry.getValue(), StandardCharsets.UTF_8)));
        return response.finalResponse();
    }

    @POST
    @Path("/integrate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response integrateUser(
            @FormParam("integration") String integrationJson) throws CustomException {
        Integration integration = gson.fromJson(integrationJson, Integration.class);
        GibDao gibdao = new GibDao(null);
        ResponseData<String> response = gibdao.integrateUser(integration);
        return response.finalResponse();
    }

    @POST
    @Path("/getDashboard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboard() {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<Dashboard> response = new ResponseData<>();
        GibDao gibdao = new GibDao(authUser.getTenantUsers().getTenant());
        Dashboard dashboard = gibdao.getDashboard(authUser);
        response.setData(dashboard);
        return response.finalResponse();
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    @POST
    @Path("sendUserCreditRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendUserCreditRequest(
            @FormParam("amount") long amount,
            @FormParam("description") String description) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibDao.sendCreditRequest(amount, description);
        return response.finalResponse();
    }

    @POST
    @Path("getUserCreditRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserCreditRequest(
            @FormParam("id") long id,
            @FormParam("vkn") String vkn,
            @FormParam("name") String name,
            @FormParam("confirmed") String confirmed,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        ResponseData<List<EInvoiceCreditRequest>> response;
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        response = gibDao.getCreditRequest(id, vkn, name, confirmed, "false", limit, offset);
        return response.finalResponse();
    }

    @POST
    @Path("acceptUserCreditRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptUserCreditRequest(
            @FormParam("id") long id) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibDao.acceptCreditRequest(id);
        return response.finalResponse();
    }

    @POST
    @Path("sendUserCredit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendUserCredit(
            @FormParam("vkn") String vkn,
            @FormParam("amount") long amount) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibDao.sendCredit(vkn, amount);
        return response.finalResponse();
    }

    @POST
    @Path("sendAdminCreditRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendAdminCreditRequest(
            @FormParam("amount") long amount,
            @FormParam("description") String description) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibDao.sendCreditRequest(amount, description);
        return response.finalResponse();
    }

    @POST
    @Path("getAdminCreditRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdminCreditRequest(
            @FormParam("id") long id,
            @FormParam("vkn") String vkn,
            @FormParam("name") String name,
            @FormParam("confirmed") String confirmed,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        ResponseData<List<EInvoiceCreditRequest>> response;
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        response = gibDao.getCreditRequest(id, vkn, name, confirmed, "true", limit, offset);
        return response.finalResponse();
    }

    @POST
    @Path("acceptAdminCreditRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptAdminCreditRequest(
            @FormParam("id") long id) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibDao.acceptCreditRequest(id);
        return response.finalResponse();
    }

    @POST
    @Path("getEinvoiceUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEinvoiceUsers(
            @FormParam("vkn") String vkn,
            @FormParam("name") String name,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        ResponseData<List<EInvoiceUser>> response;
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        response = gibDao.getEInviceUsers(0, vkn, name, "false", limit, offset);
        return response.finalResponse();
    }

    @POST
    @Path("getEinvoiceAdmins")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEinvoiceAdmins(
            @FormParam("id") long id,
            @FormParam("name") String name,
            @DefaultValue("true") @FormParam("confirmed") String confirmed,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        ResponseData<List<EInvoiceUser>> response;
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        response = gibDao.getEInviceUsers(id, null, name, "true", limit, offset);
        return response.finalResponse();
    }

    @POST
    @Path("registerEinvoiceAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerEinvoiceAdmin(
            @FormParam("name") String name,
            @FormParam("email") String email) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibDao.registerEinvoiceAdmin(name, email);
        return response.finalResponse();
    }

    @POST
    @Path("registerEinvoiceUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerEinvoiceUser(
            @FormParam("vkn") String vkn,
            @FormParam("email") String email) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibDao.registerEinvoiceUser(vkn, email);
        return response.finalResponse();
    }

    @POST
    @Path("activateEinvoice")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateEinvoice() {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        Tenant ownerTenant = new TenantDao().getSubTenants(authUser.getTenantUsers().getTenant().getOwner(), null, null, null, null, null, null, null, 1, 0).get(0);
        GibDao gibDao = new GibDao(ownerTenant);
        String einvoiceVkn = authUser.getTenantUsers().getTenant().getTenantInfo().getTc();
        String einvoiceEmail = authUser.getTenantUsers().getTenant().getEmail();
        ResponseData<String> response = gibDao.registerEinvoiceUser(einvoiceVkn, einvoiceEmail);
        return response.finalResponse();
    }

    @POST
    @Path("getReport")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReport(
            @FormParam("vkn") String vkn,
            @FormParam("startDate") String startDate,
            @FormParam("endDate") String endDate,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<List<EInvoiceReport>> response = gibDao.getReport(vkn, startDate, endDate, limit, offset);
        return response.finalResponse();
    }

    @POST
    @Path("createReport")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReport(
            @FormParam("vkn") String vkn,
            @FormParam("startDate") String startDate,
            @FormParam("endDate") String endDate) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<EInvoiceReport> response = gibDao.createReport(vkn, startDate, endDate);
        return response.finalResponse();
    }

    @POST
    @Path("sendReport")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendReport(
            @FormParam("id") String id) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        ResponseData<String> response = gibDao.sendReport(id);
        return response.finalResponse();
    }
}
