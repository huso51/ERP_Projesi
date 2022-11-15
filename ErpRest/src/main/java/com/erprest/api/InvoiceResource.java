/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import com.erprest.dao.InvoiceDao;
import com.erprest.filter.CustomException;
import com.erprest.model.Invoice;
import com.erprest.model.InvoiceException;
import com.erprest.model.InvoiceScenario;
import com.erprest.model.InvoiceWithholding;
import com.erprest.model.InvoiceType;
import com.erprest.model.ResponseData;
import com.erprest.model.User;
import com.erprest.service.DespatchInvoice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author msi_ge72
 */
@Path("sessions/invoices")
public class InvoiceResource {

    @Context
    private transient HttpServletRequest servletRequest;
    @Context
    SecurityContext securityContext;

    Gson gson = new Gson();

    @POST
    @Path("/getInvoices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoices(
            @FormParam("invoiceId") long invoiceId,
            @FormParam("invoiceNo") String invoiceNo,
            @FormParam("tenantId") String tenantId,
            @FormParam("customer") String customer,
            @FormParam("isComingInvoice") String isComingInvoice,
            @FormParam("confirmed") boolean confirmed,
            @FormParam("whereDateAfter") String whereDateAfter,
            @FormParam("whereDateBefore") String whereDateBefore,
            @FormParam("orderBy") String order_by,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<List<Invoice>> response = new ResponseData<>();
        InvoiceDao invoicedao = new InvoiceDao();
        tenantId = authUser.getTenantUsers().getTenant().getId();
        confirmed = true;
        List<Invoice> invoices = invoicedao.getInvoicesJson(invoiceId, invoiceNo, tenantId, customer, isComingInvoice, confirmed, whereDateAfter, whereDateBefore, order_by, limit, offset);
        response.setData(invoices);
        return response.finalResponse();
    }

    @POST
    @Path("/addInvoice")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addInvoice(@FormParam("invoice") String invoiceJson) throws CustomException {
        //invoiceJson="{\"invoiceLine\":[{\"id\":0,\"invoiceId\":0,\"itemId\":0,\"item\":{\"id\":19,\"tenantId\":\"00000000-0000-0000-0000-000000000000\",\"name\":\"bilgisayar\",\"uomId\":0,\"uomCode\":{\"id\":1,\"code\":\"adt\",\"description\":\"adet\"},\"category\":{\"id\":36,\"tenantId\":\"00000000-0000-0000-0000-000000000000\",\"name\":\"elektronikk\",\"description\":\"Elektronik Ürün1\"},\"categoryId\":36,\"stock\":10,\"price\":1000,\"currencyId\":0,\"currency\":{\"id\":1,\"code\":\"TRY\",\"description\":\"TL\"},\"itemTaxes\":[{\"id\":18,\"itemId\":19,\"value\":6,\"taxesTypeId\":1,\"taxesType\":{\"id\":1,\"code\":\"kdv\",\"description\":\"katma değer vergisi\",\"taxAmount\":0}},{\"id\":74,\"itemId\":19,\"value\":7,\"taxesTypeId\":2,\"taxesType\":{\"id\":2,\"code\":\"ötv\",\"description\":\"özel tüketim vergisi\",\"taxAmount\":0}}],\"created_at\":\"2017-06-21 18:00:17\"},\"quantity\":3,\"discountAmount\":0,\"price\":3402.6,\"lastPrice\":0,\"invoiceLineTaxes\":[],\"exceptionReason\":\"\",\"currencyMultiplier\":1},{\"id\":0,\"invoiceId\":0,\"itemId\":0,\"item\":{\"id\":38,\"tenantId\":\"00000000-0000-0000-0000-000000000000\",\"name\":\"pilav\",\"uomId\":0,\"uomCode\":{\"id\":2,\"code\":\"kgm\",\"description\":\"kilogram\"},\"category\":{\"id\":38,\"tenantId\":\"00000000-0000-0000-0000-000000000000\",\"name\":\"Gıda\",\"description\":\"Gıda Ürünleri\"},\"categoryId\":38,\"stock\":100,\"price\":10,\"currencyId\":0,\"currency\":{\"id\":2,\"code\":\"USD\",\"description\":\"Dolar\"},\"itemTaxes\":[{\"id\":19,\"itemId\":38,\"value\":8,\"taxesTypeId\":2,\"taxesType\":{\"id\":2,\"code\":\"ötv\",\"description\":\"özel tüketim vergisi\",\"taxAmount\":0}}],\"created_at\":\"2017-06-21 18:00:17\"},\"quantity\":2,\"discountAmount\":1,\"price\":43.2,\"lastPrice\":0,\"invoiceLineTaxes\":[],\"exceptionReason\":\"\",\"currencyMultiplier\":2}],\"invoiceType\":{\"id\":1,\"code\":\"SATIS\",\"description\":\"SATIŞ FATURASI\"},\"customer\":{\"id\":19,\"name\":\"caner20\",\"surname\":\"adads\",\"appellation\":\"asfasf\",\"fullAppellation\":\"dsagagdgdsgsgs\",\"customerTypeId\":1,\"taxAdministration\":\"asasasf\",\"tc\":\"62176687431\",\"tradeRegisterNumber\":312432524,\"createdAt\":\"2017-06-20 15:10:49\",\"mersisNumber\":\"123123ffds342\",\"basicDiscount\":0,\"creditLimit\":0,\"isAssent\":true,\"customerType\":{\"id\":1,\"name\":\"tedarikci\",\"description\":\"11\"},\"defaultAddressId\":0,\"address\":[{\"id\":45,\"addressName\":\"caner2\",\"cityId\":3,\"districtId\":5,\"neighborhoodId\":4,\"email\":\"eafaafda\",\"fax\":\"afsasdgsdg\",\"fullAddress\":\"ddfsafdgkspogdskgldspgşdsg\",\"customerId\":19,\"neighborhood\":{\"id\":4,\"name\":\"SİNANPAŞA MAH.\",\"postCode\":1720},\"city\":{\"id\":3,\"name\":\"AFYONKARAHİSAR\"},\"district\":{\"id\":5,\"name\":\"İMAMOĞLU\"}}]},\"currency\":{\"id\":1,\"code\":\"TRY\",\"description\":\"TL\"},\"subTotal\":3040,\"grossTotal\":3039.6,\"priceTotal\":3445.7999999999997,\"taxesTotal\":406.1999999999998,\"discountTotal\":0.4,\"orderNo\":\"243242\",\"waybillNumber\":\"523532\",\"sn1\":\"233\",\"sn2\":\"2017\",\"description\":\"rfsdfsd\",\"orderDate\":\"2017-07-10\"}";
        ResponseData<Response> response = new ResponseData<>();
        Invoice invoice = gson.fromJson(invoiceJson, Invoice.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        InvoiceDao invoicedao = new InvoiceDao();
        invoice.setTenant(authUser.getTenantUsers().getTenant());
        List<Invoice> invoices = invoicedao.getInvoicesJson(0,
                invoice.getSn1() + invoice.getSn2() + (invoice.getSn3() == null ? "" : invoice.getSn3()),
                invoice.getTenant().getId(), null, "false", true, null, null, null, 1, 0);
        if (!invoices.isEmpty()) {
            response.setError(406, "Bu fatura zaten kayıtlı");
        } else {
            invoice.setSn1(invoice.getSn1().toUpperCase());
            if (invoice.getOrderNo() == null) {
                invoice.setOrderDate(null);
            }
            if (invoice.getWaybillNumber() == null) {
                invoice.setWaybillDate(null);
            }
            if (invoice.getSn3() == null) {
                invoice.setSn3("");
            }
            invoicedao.addInvoice(authUser, invoice);
            response.succeed("Fatura ekleme tamamlandı.");
        }
        return response.finalResponse();
    }

    @POST
    @Path("/getInvoiceType")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoiceType() {
        ResponseData<List<InvoiceType>> response = new ResponseData<>();
        InvoiceDao invoicedao = new InvoiceDao();
        List<InvoiceType> typess = invoicedao.getInvoiceTypes();
        response.setData(typess);
        return response.finalResponse();
    }

    @POST
    @Path("/getWithholdings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTevkifatType() {
        ResponseData<List<InvoiceWithholding>> response = new ResponseData<>();
        InvoiceDao invoicedao = new InvoiceDao();
        List<InvoiceWithholding> tevkifats = invoicedao.getTevkifatTypes();
        response.setData(tevkifats);
        return response.finalResponse();
    }

    @POST
    @Path("/getExceptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExceptionType() {
        ResponseData<List<InvoiceException>> response = new ResponseData<>();
        InvoiceDao invoicedao = new InvoiceDao();
        List<InvoiceException> exceptions = invoicedao.getExceptionTypes();
        response.setData(exceptions);
        return response.finalResponse();
    }

    @GET
    @Path("/getInvoiceHtml")
    @Produces(MediaType.TEXT_HTML)
    public Response getInvoiceHtml(@QueryParam("invoiceId") long invoiceId) {
        InvoiceDao invoicedao = new InvoiceDao();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<String> response = new ResponseData<>();
        String invoiceHtml = invoicedao.getInvoiceHtml(invoiceId, authUser.getTenantUsers().getTenant().getId());
        response.setData(invoiceHtml);
        return response.finalResponse();
    }

    @POST
    @Path("/getInvoicePdf")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getInvoicePdf(@FormParam("invoices") String invoiceJson) {
        ResponseData<HashMap<String, byte[]>> response = new ResponseData<>();
        List<Invoice> invoices = gson.fromJson(invoiceJson, new TypeToken<List<Invoice>>() {
        }.getType());
        InvoiceDao invoicedao = new InvoiceDao();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        HashMap<String, byte[]> pdfFile = invoicedao.getInvoicePdf(invoices, authUser.getTenantUsers().getTenant().getId());
        response.setData(pdfFile);
        return response.finalResponse();
    }

    @POST
    @Path("/sendToCustomers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendToCustomers(@FormParam("invoices") String invoiceJson) {
        List<Invoice> invoices = gson.fromJson(invoiceJson, new TypeToken<List<Invoice>>() {
        }.getType());
        ResponseData<Response> response = new ResponseData<>();
        List<Invoice> extendedInvoice = new ArrayList<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        InvoiceDao invoicedao = new InvoiceDao();
        for (int i = 0; i < invoices.size(); i++) {
            List<Invoice> tempInvoice = invoicedao.getInvoicesJson(invoices.get(i).getId(), null, authUser.getTenantUsers().getTenant().getId(), null, null, true, null, null, null, 1, 0);
            extendedInvoice.add(tempInvoice.get(0));
        }
        invoicedao.sendToCustomer(extendedInvoice, authUser.getTenantUsers().getTenant().getId());
        response.succeed("Faturalar alıcılarına gönderildi.");
        return response.finalResponse();
    }

    @POST
    @Path("/deleteInvoice")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteInvoice(@FormParam("invoices") String invoiceJson) {
        List<Invoice> invoices = gson.fromJson(invoiceJson, new TypeToken<List<Invoice>>() {
        }.getType());
        ResponseData<Response> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        InvoiceDao invoicedao = new InvoiceDao();
        invoicedao.deleteInvoice(invoices, authUser.getTenantUsers().getTenant().getId());
        response.succeed("Fatura silme işlemi tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/getInvoiceScenario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoiceScenario() {
        ResponseData<List<InvoiceScenario>> response = new ResponseData<>();
        InvoiceDao invoicedao = new InvoiceDao();
        List<InvoiceScenario> scenarios = invoicedao.getInvoiceScenario();
        response.setData(scenarios);
        return response.finalResponse();
    }

    @POST
    @Path("/getIncrementSn3")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIncrementSn3(
            @FormParam("sn1") String sn1,
            @FormParam("sn2") String sn2) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<String> response = new ResponseData<>();
        InvoiceDao invoiceDao = new InvoiceDao();
        String sn3 = invoiceDao.getIncrementSn3(authUser.getTenantUsers().getTenant().getId(), sn1, sn2);
        response.setData(sn3);
        return response.finalResponse();
    }

    @GET
    @Path("/getDespatch")
    @Produces(MediaType.APPLICATION_XML)
    public String getDespatch(@QueryParam("id") long invoiceId) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        InvoiceDao invoiceDao=new InvoiceDao();
        Invoice invoice = invoiceDao.getInvoicesJson(invoiceId, null, authUser.getTenantUsers().getTenant().getId(), null, null, true, null, null, null, 1, 0).get(0);
        DespatchInvoice despatchXml=new DespatchInvoice();
        return despatchXml.getXml(invoice);
    }

}
