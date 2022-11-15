/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import com.erprest.dao.AccountDao;
import com.erprest.filter.CustomException;
import com.erprest.model.AccountActivity;
import com.erprest.model.CustomerPayment;
import com.erprest.model.PaymentBillInstallment;
import com.erprest.model.PaymentCheckbookItem;
import com.erprest.model.ResponseData;
import com.erprest.model.SpendingType;
import com.erprest.model.TenantAccount;
import com.erprest.model.User;
import com.google.gson.Gson;
import java.util.List;
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
@Path("sessions/account")
public class AccountResource {

    @Context
    private transient HttpServletRequest servletRequest;
    Gson gson = new Gson();

    @POST
    @Path("/getAccounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts(
            @FormParam("id") long id,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        ResponseData<List<TenantAccount>> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        List<TenantAccount> accounts = accountDao.getTenantAccounts(authUser.getTenantUsers().getTenant().getId(), id, limit, offset);
        response.setData(accounts);
        return response.finalResponse();
    }

    @POST
    @Path("/addAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAccount(@FormParam("tenantAccount") String tenantAccountJson) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        TenantAccount account = gson.fromJson(tenantAccountJson, TenantAccount.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        account.setTenant(authUser.getTenantUsers().getTenant());
        accountDao.addAccount(account, authUser);
        response.succeed("Hesap ekleme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/updateAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(@FormParam("tenantAccount") String tenantAccountJson) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        TenantAccount account = gson.fromJson(tenantAccountJson, TenantAccount.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        account.setTenant(authUser.getTenantUsers().getTenant());
        accountDao.updateAccount(account, authUser);
        response.succeed("Hesap güncelleme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/deleteAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(@FormParam("tenantAccount") String tenantAccountJson) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        TenantAccount account = gson.fromJson(tenantAccountJson, TenantAccount.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        account.setTenant(authUser.getTenantUsers().getTenant());
        accountDao.deleteAccount(account);
        response.succeed("Hesap silme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/getActivities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActivities(
            @FormParam("accountId") long accountId,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        ResponseData<List<AccountActivity>> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        List<AccountActivity> accounts = accountDao.getActivities(accountId, limit, offset);
        response.setData(accounts);
        return response.finalResponse();
    }

    @POST
    @Path("addAccountAmount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAccountAmount(
            @FormParam("tenantAccount") String tenantAccountJson,
            @FormParam("amount") double amount) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        TenantAccount account = gson.fromJson(tenantAccountJson, TenantAccount.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        account.setTenant(authUser.getTenantUsers().getTenant());
        AccountDao accountDao = new AccountDao();
        List<TenantAccount> existingAccounts = accountDao.getTenantAccounts(account.getTenant().getId(), account.getId(), 1, 0);
        if (existingAccounts.isEmpty()) {
            throw new CustomException(account.getTenant().getName() + " organizasyonuna ait " + account.getName() + " hesabı bulunamadı.");
        }
        if (existingAccounts.get(0).getAmount() < Math.abs(amount) && amount < 0) {
            throw new CustomException("En fazla" + existingAccounts.get(0).getAmount() + " kadar para çekebilirsiniz.");
        }
        AccountActivity activity = new AccountActivity();
        activity.setAccount(account);
        activity.setUser(authUser);
        if (amount > 0) {
            activity.setDebt(amount);
            activity.setProcess("Para Girişi");
            activity.setDescription("Hesaba Para Girişi");
        } else {
            activity.setReceivable(amount);
            activity.setProcess("Para Çıkışı");
            activity.setDescription("Hesaptan Para Çıkışı");
        }
        accountDao.addAccountActivity(activity);
        response.succeed("İşlem tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("addAccountTransfer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAccountTransfer(
            @FormParam("fromAccount") String fromAccountJson,
            @FormParam("toAccount") String toAccountJson,
            @FormParam("amount") double amount) throws CustomException {
        ResponseData<List<AccountActivity>> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        TenantAccount fromAccount = gson.fromJson(fromAccountJson, TenantAccount.class);
        TenantAccount toAccount = gson.fromJson(toAccountJson, TenantAccount.class);
        fromAccount.setTenant(authUser.getTenantUsers().getTenant());
        toAccount.setTenant(authUser.getTenantUsers().getTenant());
        AccountDao accountDao = new AccountDao();
        accountDao.addAccountTransfer(authUser, fromAccount, toAccount, amount);
        response.succeed("Hesaplar arası transfer tamamlandı.");
        return response.finalResponse();
    }

    // ---------------------Masraf Kalemi
    @POST
    @Path("/getSpendingTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpendingType() {
        ResponseData<List<SpendingType>> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        List<SpendingType> types = accountDao.getSpendingTypes(authUser.getTenantUsers().getTenant().getId());
        response.setData(types);
        return response.finalResponse();
    }

    @POST
    @Path("/addSpendingType")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSpendingType(@FormParam("spendingType") String spendingTypeJson) {
        ResponseData<Response> response = new ResponseData<>();
        SpendingType spendingType = gson.fromJson(spendingTypeJson, SpendingType.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        spendingType.setTenant(authUser.getTenantUsers().getTenant());
        accountDao.addSpendingType(spendingType);
        response.succeed("Masraf kalemi ekleme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/updateSpendingType")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSpendingType(@FormParam("spendingType") String spendingTypeJson) {
        ResponseData<Response> response = new ResponseData<>();
        SpendingType spendingType = gson.fromJson(spendingTypeJson, SpendingType.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        spendingType.setTenant(authUser.getTenantUsers().getTenant());
        accountDao.updateSpendingType(spendingType);
        response.succeed("Masraf kalemi güncelleme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/deleteSpendingType")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSpendingType(@FormParam("spendingType") String spendingTypeJson) {
        ResponseData<Response> response = new ResponseData<>();
        SpendingType spendingType = gson.fromJson(spendingTypeJson, SpendingType.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        spendingType.setTenant(authUser.getTenantUsers().getTenant());
        accountDao.deleteSpendingType(spendingType);
        response.succeed("Masraf kalemi silme tamamlandı.");
        return response.finalResponse();
    }

    //--------------------Customer Payments
    @POST
    @Path("/addCustomerPayment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCustomerPayment(@FormParam("customerPayment") String customerPaymentJson) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        CustomerPayment payment = gson.fromJson(customerPaymentJson, CustomerPayment.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        payment.setTenant(authUser.getTenantUsers().getTenant());
        accountDao.addCustomerPayment(payment, authUser);
        response.succeed("Hesap hareketliliği eklendi.");
        return response.finalResponse();
    }

    @POST
    @Path("/getCustomerPayments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerPayments(@FormParam("customerId") long customerId,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        ResponseData<List<CustomerPayment>> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        List<CustomerPayment> payments = accountDao.getCustomerPayments(authUser.getTenantUsers().getTenant(), customerId, limit, offset);
        response.setData(payments);
        return response.finalResponse();
    }

    @POST
    @Path("/getCheckbooks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCheckbooks() {
        ResponseData<List<PaymentCheckbookItem>> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        List<PaymentCheckbookItem> checkbooks = accountDao.getCheckBooks(authUser.getTenantUsers().getTenant());
        response.setData(checkbooks);
        return response.finalResponse();
    }

    @POST
    @Path("/payCheckbook")
    @Produces(MediaType.APPLICATION_JSON)
    public Response payChecbook(@FormParam("paymentCheckbookItem") String paymentCheckbookItemJson) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        PaymentCheckbookItem checkbook = gson.fromJson(paymentCheckbookItemJson, PaymentCheckbookItem.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        accountDao.payCheckbook(authUser, checkbook);
        response.succeed("Çek ödemesi tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/getBillInstallments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBillInstallments() {
        ResponseData<List<PaymentBillInstallment>> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        List<PaymentBillInstallment> checkbooks = accountDao.getBillInstallments(authUser.getTenantUsers().getTenant());
        response.setData(checkbooks);
        return response.finalResponse();
    }

    @POST
    @Path("/payBillInstallment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response payBillInstallment(@FormParam("paymentBillInstallment") String paymentBillInstallmentJson) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        PaymentBillInstallment installment = gson.fromJson(paymentBillInstallmentJson, PaymentBillInstallment.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        AccountDao accountDao = new AccountDao();
        accountDao.payInstallment(authUser, installment);
        response.succeed("Senet ödemesi tamamlandı.");
        return response.finalResponse();
    }

}
