/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import com.erprest.connection.EMailConnectionHelper;
import com.erprest.dao.TenantDao;
import com.erprest.dao.UserDao;
import com.erprest.filter.CustomException;
import com.erprest.model.EmailConnection;
import com.erprest.model.ExpireRequest;
import com.erprest.model.ResponseData;
import com.erprest.model.Tenant;
import com.erprest.model.TenantPrefix;
import com.erprest.model.TenantUsers;
import com.erprest.model.User;
import com.erprest.model.UserRole;
import com.google.gson.Gson;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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
@Path("sessions/tenants")
public class TenantResource {

    @Context
    private transient HttpServletRequest servletRequest;

    Gson gson = new Gson();

    @POST
    @Path("/getUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(
            @FormParam("whereId") String whereId,
            @FormParam("whereName") String whereName,
            @FormParam("whereEmail") String whereEmail,
            @FormParam("whereDateAfter") String whereDateAfter,
            @FormParam("whereDateBefore") String whereDateBefore,
            @FormParam("whereStatus") String whereStatus,
            @FormParam("orderBy") String order_by,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        ResponseData<List<User>> response = new ResponseData<>();
        List<User> userlist = tenantdao.getTenantUsers(authUser, whereId, whereName, whereEmail, whereDateAfter, whereDateBefore, whereStatus, order_by, limit, offset);
        response.setData(userlist);
        return response.finalResponse();
    }

    @POST
    @Path("/getSubTenants")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubTenants(
            @FormParam("whereId") String whereId,
            @FormParam("whereName") String whereName,
            @FormParam("whereStatus") String whereStatus,
            @FormParam("orderBy") String order_by,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        ResponseData<List<Tenant>> response = new ResponseData<>();
        String ownerId = null;
        if (!authUser.getTenantUsers().getTenant().getTenantType().equals("SUPERADMIN")) {
            ownerId = authUser.getTenantUsers().getTenant().getId();
        }
        List<Tenant> tenants = tenantdao.getSubTenants(whereId, whereName, ownerId, null, null, null, whereStatus, order_by, limit, offset);
        response.setData(tenants);
        return response.finalResponse();
    }

    @POST
    @Path("/addSubTenant")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubTenant(
            @FormParam("tenant") String tenantJson) throws CustomException {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        UserDao userdao = new UserDao();
        Tenant newTenant = gson.fromJson(tenantJson, Tenant.class);
        ResponseData<Response> response = new ResponseData<>();
        if (!(newTenant.getTenantType().equals("RESELLER") || newTenant.getTenantType().equals("CUSTOMER"))
                || authUser.getTenantUsers().getTenant().getTenantType().equals("CUSTOMER")) {
            response.setError(401, "yetki hatası");
        }
        if (!userdao.isValidEmailAddress(newTenant.getEmail())) {
            response.setError(406, "Geçerli bir email adresi giriniz.");
            return response.finalResponse();
        }
        /*if (authUser.getTenantUsers().getTenant().getCredits() < newTenant.getCredits()
                && !authUser.getTenantUsers().getTenant().getTenantType().equals("SUPERADMIN")) {
            response.getError().setStatus("406");
            response.getError().setMessage("Kredi vermek için yeterli krediniz bulunmamaktadır.");
            return response.finalResponse();
        }*/
        newTenant.setId(UUID.randomUUID().toString());
        newTenant.setOwner(authUser.getTenantUsers().getTenant().getId());
        newTenant.setExpireCount(15);

        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString());
        Random rand = new Random();
        newUser.setPassword("" + rand.nextInt(800000) + 100000);
        newUser.setEmail(newTenant.getEmail());
        newUser.setName("ADMİN");
        newUser.setDescription("SUPERADMİN");
        newUser.setStatus("DISABLED");

        UserRole role = new UserRole();
        role.setUser("a");
        role.setCustomer("a");
        role.setTenant("a");
        role.setItem("a");
        role.setInvoice("a");
        role.setAccount("a");
        role.setIsTenantAdmin(true);
        role.setIsEinvoiceAdmin(false);
        newUser.setTenantUsers(new TenantUsers());
        newUser.getTenantUsers().setUserRole(role);
        newUser.getTenantUsers().setTenant(newTenant);
        tenantdao.addSubTenant(authUser, newUser);
        response.succeed("Organizasyon ekleme tamamlandı. " + newTenant.getEmail() + " adresine mail gönderildi.");
        return response.finalResponse();
    }

    @POST
    @Path("/updateTenantUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTenantUsers(
            @FormParam("user") String userJson) throws CustomException {
        TenantDao tenantdao = new TenantDao();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<Response> response = new ResponseData<>();
        User user = gson.fromJson(userJson, User.class);
        if (!authUser.getTenantUsers().getUserRole().isIsTenantAdmin()) {
            response.setError(401, "Admin yetkiniz bulunmamaktadır.");
            return response.finalResponse();
        }
        User originalUser = new UserDao().getUserInfo(user.getId());
        user.getTenantUsers().getUserRole().setIsEinvoiceAdmin(
                originalUser.getTenantUsers().getUserRole().isIsEinvoiceAdmin());
        tenantdao.updateTenantUsers(user, authUser.getTenantUsers().getTenant().getId());
        response.succeed("Güncelleme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/updateTenant")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTenant(
            @FormParam("tenant") String tenantJson) throws CustomException {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<Response> response = new ResponseData<>();
        TenantDao tenantdao = new TenantDao();
        Tenant tenant = gson.fromJson(tenantJson, Tenant.class);
        /*if (authUser.getTenantUsers().getTenant().getCredits() < tenant.getCredits()
                && !authUser.getTenantUsers().getTenant().getTenantType().equals("SUPERADMIN")) {
            response.getError().setStatus("406");
            response.getError().setMessage("Kredi vermek için yeterli krediniz bulunmamaktadır.");
            return response.finalResponse();
        }*/
        tenantdao.updateTenant(authUser.getTenantUsers().getTenant(), tenant);
        response.succeed("Organizasyon güncellendi.");
        return response.finalResponse();
    }

    @POST
    @Path("/addPrefix")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPrefix(@FormParam("prefix") String prefixJson) {
        ResponseData<Response> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        TenantPrefix tenantPrefix = gson.fromJson(prefixJson, TenantPrefix.class);
        tenantPrefix.setTenantId(authUser.getTenantUsers().getTenant().getId());
        tenantdao.addPrefix(tenantPrefix);
        response.succeed("Fatura ön eki tanımlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/updatePrefix")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePrefix(@FormParam("prefix") String prefixJson) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        TenantPrefix tenantPrefix = gson.fromJson(prefixJson, TenantPrefix.class);
        tenantPrefix.setTenantId(authUser.getTenantUsers().getTenant().getId());
        tenantdao.updatePrefix(tenantPrefix);
        response.succeed("Fatura ön eki güncellendi.");
        return response.finalResponse();
    }

    @POST
    @Path("/getPrefix")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrefix() {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        ResponseData<List<TenantPrefix>> response = new ResponseData<>();
        List<TenantPrefix> prefixes = tenantdao.getPrefix(authUser.getTenantUsers().getTenant().getId());
        response.setData(prefixes);
        return response.finalResponse();
    }

    @POST
    @Path("/deletePrefix")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePrefix(@FormParam("prefix") String prefixJson) {
        ResponseData<Response> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        TenantPrefix tenantPrefix = gson.fromJson(prefixJson, TenantPrefix.class);
        tenantPrefix.setTenantId(authUser.getTenantUsers().getTenant().getId());
        tenantdao.deletePrefix(tenantPrefix);
        response.succeed("Fatura ön eki silindi.");
        return response.finalResponse();
    }

    @POST
    @Path("/getUserTenant")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTenant() {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<Tenant> response = new ResponseData<>();
        UserDao userdao = new UserDao();
        Tenant tenant = userdao.getUserInfo(authUser.getId()).getTenantUsers().getTenant();
        response.setData(tenant);
        return response.finalResponse();
    }

    /*@POST
    @Path("/expireRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response expireRequest(
            @FormParam("amount") long amount,
            @FormParam("description") String description) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<Response> response = new ResponseData<>();
        TenantDao tenantdao = new TenantDao();
        try {
            response = tenantdao.expireRequest(authUser.getTenantUsers().getTenant(), amount, description);
        } catch (WebApplicationException ex) {
            response.getError().setStatus("500");
            response.getError().setMessage(ex.getMessage());
        }
        return response.finalResponse();
    }*/
    @POST
    @Path("/showExpireRequests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response showExpireRequest(@FormParam("confirmed") boolean confirmed) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<List<ExpireRequest>> response = new ResponseData<>();
        TenantDao tenantdao = new TenantDao();
        List<ExpireRequest> requests = tenantdao.showExpireRequest(confirmed);
        response.setData(requests);
        return response.finalResponse();
    }

    @POST
    @Path("/acceptExpireRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptExpireRequest(
            @FormParam("id") long expireId) {
        ResponseData<Response> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        tenantdao.acceptExpireRequest(expireId);
        response.succeed("İstek kabul edildi.");
        return response.finalResponse();
    }

    @POST
    @Path("/emailSetup")
    @Produces(MediaType.APPLICATION_JSON)
    public Response emailSetup(@FormParam("emailConnection") String emailJson) {
        ResponseData<Response> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        EmailConnection emailConnection = gson.fromJson(emailJson, EmailConnection.class);
        emailConnection.setTenantId(authUser.getTenantUsers().getTenant().getId());
        tenantdao.emailSetup(emailConnection);
        response.succeed("Email gönderim bilgileri kaydedildi.");
        return response.finalResponse();
    }

    @POST
    @Path("/emailSetting")
    @Produces(MediaType.APPLICATION_JSON)
    public Response emailSetting() {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        ResponseData<EmailConnection> response = new ResponseData<>();
        EmailConnection durum = tenantdao.emailSetting(authUser.getTenantUsers().getTenant().getId());
        response.setData(durum);
        return response.finalResponse();
    }

    @POST
    @Path("/emailTest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response emailTest(@FormParam("emailTo") String emailTo) {
        ResponseData<Response> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantDao tenantdao = new TenantDao();
        EmailConnection emailConnection = tenantdao.emailSetting(authUser.getTenantUsers().getTenant().getId());
        EMailConnectionHelper econ = new EMailConnectionHelper(emailConnection);
        econ.sendTestMessage(emailTo);
        response.succeed(emailTo + " adresine Test Email i gönderildi.");
        return response.finalResponse();
    }

}
