/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import com.erprest.dao.AuthDao;
import com.erprest.dao.GibDao;
import com.erprest.dao.UserDao;
import com.erprest.filter.CustomException;
import com.erprest.model.GibUser;
import com.erprest.model.ResponseData;
import com.erprest.model.TenantUsers;
import com.erprest.model.User;
import com.google.gson.Gson;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
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
@Path("sessions/users")
public class UserResource {

    @Context
    private transient HttpServletRequest servletRequest;

    Gson gson = new Gson();

    @POST
    @Path("/addUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(
            @FormParam("user") String userJson
    ) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        User newUser = gson.fromJson(userJson, User.class);
        UserDao userdao = new UserDao();

        newUser.getTenantUsers().setTenant(authUser.getTenantUsers().getTenant());
        newUser.setId(UUID.randomUUID().toString());
        newUser.setStatus("ENABLED");
        if (!userdao.isValidEmailAddress(newUser.getEmail())) {
            response.setError(406, "Geçerli bir email adresi giriniz.");
        } else {
            userdao.addUser(authUser, newUser);
            response.succeed(newUser.getEmail() + " adresine doğrulama maili gönderildi.");
        }
        return response.finalResponse();
    }

    @POST
    @Path("/updateUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("password") String password,
            @FormParam("description") String description,
            @FormParam("status") String status,
            @FormParam("defaultTenantId") String defaultTenant,
            @HeaderParam("Authorization") String authorization
    ) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<User> response = new ResponseData<>();
        UserDao userdao = new UserDao();
        AuthDao authdao = new AuthDao();

        User user = new User();
        user.setId(authUser.getId());
        user.setName(name);
        user.setDescription(description);
        user.setStatus(status);
        user.setEmail(email);
        if (password == null) {
            user.setPassword(authdao.parseAuthentication(authorization).getPassword());
        } else {
            user.setPassword(password);
        }
        if (user.getEmail() != null && !userdao.isValidEmailAddress(user.getEmail())) {
            response.setError(406, "Geçerli bir email adresi giriniz.");
        } else {
            User updatedUser = userdao.updateUser(user, defaultTenant);
            updatedUser.setRememberToken("Basic " + new String(Base64.getEncoder().encode((updatedUser.getEmail() + ":" + user.getPassword()).getBytes())));
            response.setData(updatedUser);
        }
        return response.finalResponse();
    }

    @POST
    @Path("/getUserTenants")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubTenants() {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<List<TenantUsers>> response = new ResponseData<>();
        UserDao userDao = new UserDao();
        List<TenantUsers> tenants = userDao.getUserTenants(authUser);
        response.setData(tenants);
        return response.finalResponse();
    }

    @POST
    @Path("/getGibUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGibUsers(
            @FormParam("vkn") String vknNumber,
            @DefaultValue("pk") @FormParam("aliasType") String aliasType) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<List<GibUser>> response = new ResponseData<>();
        if (vknNumber == null) {
            vknNumber = authUser.getTenantUsers().getTenant().getTenantInfo().getTc();
        }
        GibDao gibDao = new GibDao(authUser.getTenantUsers().getTenant());
        List<GibUser> gibUsers = gibDao.getGibUsers(vknNumber, aliasType);
        response.setData(gibUsers);
        return response.finalResponse();
    }

    @POST
    @Path("/deleteUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@FormParam("userId") String userId) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<Response> response = new ResponseData<>();
        UserDao userdao = new UserDao();
        if (authUser.getId().equals(userId) || !authUser.getTenantUsers().getUserRole().isIsTenantAdmin()) {
            response.setError(401, "Kullanıcı silme yetkiniz bulunmamaktadır.");
        } else {
            userdao.deleteUser(authUser, userId);
            response.succeed("Kullanıcı silindi.");
        }
        return response.finalResponse();
    }

    @POST
    @Path("/getUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser() {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<User> response = new ResponseData<>();
        response.setData(authUser);
        return response.finalResponse();
    }
}
