/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import com.erprest.dao.AuthDao;
import com.erprest.dao.GibDao;
import com.erprest.dao.TenantDao;
import com.erprest.dao.UserDao;
import com.erprest.filter.CustomException;
import com.erprest.model.Address;
import com.erprest.model.City;
import com.erprest.model.Customer;
import com.erprest.model.CustomerType;
import com.erprest.model.District;
import com.erprest.model.Neighborhood;
import com.erprest.model.ResponseData;
import com.erprest.model.Tenant;
import com.erprest.model.TenantUsers;
import com.erprest.model.User;
import com.erprest.model.UserRole;
import com.google.gson.Gson;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author msi_ge72
 */
@Path("/auth")
public class AuthenticationResource {

    Gson gson = new Gson();

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(
            @FormParam("tc") String tc,
            @FormParam("name") String name,
            @FormParam("surname") String surname,
            @FormParam("email") String email) throws CustomException {
        TenantDao tenantdao = new TenantDao();
        UserDao userdao = new UserDao();
        ResponseData<Response> response = new ResponseData<>();
        Tenant newTenant = new Tenant();

        newTenant.setEmail(email);
        if (!userdao.isValidEmailAddress(newTenant.getEmail())) {
            response.setError(0406, "Geçerli bir email adresi giriniz.");
            return response.finalResponse();
        }
        newTenant.setId(UUID.randomUUID().toString());
        newTenant.setTenantType("CUSTOMER");
        Customer tenantInfo = new Customer();
        if (tc.length() == 11) {
            tenantInfo.setName(name);
            tenantInfo.setSurname(surname);
        } else if (tc.length() == 10) {
            tenantInfo.setAppellation(name);
            tenantInfo.setFullAppellation(surname);
        }
        newTenant.setName(name);
        tenantInfo.setTc(tc);
        Address address = new Address();
        address.setEmail(email);
        address.setCity(new City());
        address.setDistrict(new District());
        address.setNeighborhood(new Neighborhood());
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        tenantInfo.setAddress(addresses);
        CustomerType customerType = new CustomerType();
        customerType.setId(2);
        tenantInfo.setCustomerType(customerType);
        newTenant.setTenantInfo(tenantInfo);
        newTenant.setExpireCount(15);

        Tenant authTenant = (tenantdao.getSubTenants(null, null, null, null, "SUPERADMIN", null, null, null, 1, 0).get(0));
        newTenant.setOwner(authTenant.getId());
        User authUser = new User();
        authUser.setTenantUsers(new TenantUsers());
        UserRole userRole = new UserRole();
        userRole.setIsTenantAdmin(true);
        authUser.getTenantUsers().setUserRole(userRole);
        authUser.getTenantUsers().setTenant(authTenant);

        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString());
        Random rand = new Random();
        newUser.setPassword("" + rand.nextInt(800000) + 100000);
        newUser.setEmail(newTenant.getEmail());
        newUser.setName("ADMİN");
        newUser.setDescription("SUPERADMİN");
        newUser.setStatus("DISABLED");

        newUser.setTenantUsers(new TenantUsers());
        newUser.getTenantUsers().setTenant(newTenant);
        UserRole role = new UserRole();
        role.setUser("a");
        role.setCustomer("a");
        role.setTenant("a");
        role.setItem("a");
        role.setInvoice("a");
        role.setAccount("a");
        role.setIsTenantAdmin(true);
        role.setIsEinvoiceAdmin(false);
        newUser.getTenantUsers().setUserRole(role);
        tenantdao.addSubTenant(authUser, newUser);
        response.succeed(email + " adresine doğrulama maili gönderildi.");
        return response.finalResponse();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @FormParam("email") String email,
            @FormParam("password") String password) {
        ResponseData<User> response = new ResponseData<>();
        if (email == null || password == null) {
            response.setError(406, "Email yada Password boş olamaz");
            return response.finalResponse();
        }
        AuthDao auth = new AuthDao();
        UserDao userdao = new UserDao();
        User user = auth.login(email, password);

        if (user != null && user.getId() != null) {
            User user2 = userdao.getUserInfo(user.getId());
            user2.setRememberToken("Basic " + new String(Base64.getEncoder().encode((email + ":" + password).getBytes())));
            Date expireDate;
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                expireDate = df.parse(user2.getTenantUsers().getTenant().getExpireDate());
            } catch (ParseException e) {
                throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
            }
            if (user2.getStatus().equals("DISABLED")) {
                response.setError(401, "Hesabınız kapatılmıştır.Lütfen yöneticinize başvurun");
            } else if (user2.getTenantUsers().getTenant().getStatus().equals("DISABLED")) {
                response.setError(401, "Organizasyonunuz kapatılmıştır.Lütfen yöneticinize başvurun");
            } else if (new Date().compareTo(expireDate) > 0) {
                response.setError(401, "Erp portalını kullanma süreniz dolmuştur.");
            } else {
                //user2.getTenantUsers().getTenant().setDashboard(gibDao.getDashboard(user2.getTenantUsers().getTenant()));
                response.setData(user2);
            }
        } else {
            response.setError(401, "Kullanıcı adı veya şifre yanlış");
        }
        return response.finalResponse();
    }

    @POST
    @Path("/verifyCode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyConfirmationCode(
            @FormParam("verifyCode") String confirmationCode,
            @FormParam("password") String password,
            @FormParam("action") String action) throws CustomException {
        ResponseData<String> response = new ResponseData<>();
        AuthDao authdao = new AuthDao();
        User parsedUser = authdao.parseAuthentication(confirmationCode);
        if (action.equals("confirmation")) {
            authdao.checkConfirmatinCode(parsedUser.getEmail(), parsedUser.getPassword(), password);
            response.succeed("Hesap doğrulama tamamlandı.");
        } else if (action.equals("password")) {
            authdao.checkPasswordCode(parsedUser.getEmail(), parsedUser.getPassword(), password);
            response.succeed("Şifre değiştirme tamamlandı.");
        } else if (action.equals("einvoiceAdmin")) {
            response = new GibDao(null).confirmEinvoiceAdmin(confirmationCode, password);
        } else {
            response.setError(406, "Hatalı action parametresi girildi.");
        }
        return response.finalResponse();
    }

    @POST
    @Path("/sendPasswordCode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendPasswordCode(@FormParam("email") String email) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        AuthDao authdao = new AuthDao();
        authdao.sendPasswordCode(email);
        response.succeed(email + " adresine şifre değiştirme maili gönderildi.");
        return response.finalResponse();
    }

}
