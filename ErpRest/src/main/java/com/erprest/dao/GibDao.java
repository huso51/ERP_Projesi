/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.dao;

import com.erprest.connection.ProjectConfig;
import com.erprest.filter.CustomException;
import com.erprest.model.Config;
import com.erprest.model.Customer;
import com.erprest.model.Dashboard;
import com.erprest.model.EInvoiceReport;
import com.erprest.model.EInvoiceCreditRequest;
import com.erprest.model.EInvoiceUser;
import com.erprest.model.GibInbox;
import com.erprest.model.GibUser;
import com.erprest.model.Integration;
import com.erprest.model.Invoice;
import com.erprest.model.Item;
import com.erprest.model.ResponseData;
import com.erprest.model.Tenant;
import com.erprest.model.User;
import com.erprest.service.HttpUtilities;
import com.erprest.service.InvoiceXml;
import com.erprest.service.TokenUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author msi_ge72
 */
public class GibDao {

    Config config;
    private String entegratorBaseUrl;
    private String inboxUrl, outboxUrl, userUrl, adminUrl, archiveUrl;
    private static final Logger logger = Logger.getLogger(GibDao.class.getName());
    String token;
    Tenant authTenant;

    public GibDao(Tenant authTenant) {
        this.config = ProjectConfig.getInstance().getConfig();
        this.entegratorBaseUrl = config.getEntegratorBaseUrl();
        this.inboxUrl = entegratorBaseUrl + "inbox";
        this.outboxUrl = entegratorBaseUrl + "outbox";
        this.userUrl = entegratorBaseUrl + "user";
        this.adminUrl = entegratorBaseUrl + "admin";
        this.archiveUrl = entegratorBaseUrl + "earchive";
        if (authTenant != null) {
            this.token = new TokenUtilities().getToken(
                    authTenant.getEinvoiceUsername(),
                    authTenant.getEinvoicePassword());
        }
        this.authTenant = authTenant;
    }

    Gson gson = new Gson();

    public List<GibInbox> getInvoices(boolean isOutbox, String id, String vkn,
            String profile, String startDate,
            String endDate, long limit, long offset) {
        int responseCode = 0;
        String responseGib = null;
        String urlParameters = "?1=1";
        if (startDate != null) {
            urlParameters += "&startDate=" + startDate;
        }
        if (endDate != null) {
            urlParameters += "&endDate=" + endDate;
        }
        if (id != null) {
            urlParameters += "&id=" + id;
        }
        if (vkn != null && !vkn.equals("")) {
            if (isOutbox) {
                urlParameters += "&receiverVkn=" + vkn;
            } else {
                urlParameters += "&senderVkn=" + vkn;
            }
        }
        if (profile != null) {
            urlParameters += "&profile=" + profile;
        }
        if (limit != 0) {
            urlParameters += "&limit=" + limit;
        }
        if (offset != 0) {
            urlParameters += "&offset=" + offset;
        }
        try {
            String serviceUrl;
            if (isOutbox) {
                serviceUrl = outboxUrl;
            } else {
                serviceUrl = inboxUrl;
            }
            URL obj = new URL(serviceUrl + urlParameters);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Authorization", token);
            con.setRequestMethod("GET");
            responseCode = con.getResponseCode();
            byte[] responsebyte = IOUtils.toByteArray(con.getInputStream());
            responseGib = new String(responsebyte, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        if (responseCode == 200) {
            ResponseData<List<GibInbox>> gibInboxResponse = gson.fromJson(responseGib, new TypeToken<ResponseData<List<GibInbox>>>() {
            }.getType());
            return gibInboxResponse.getData();
        } else {
            logger.log(Level.SEVERE, "Entegrator ResponseCode= {0}, Response={1}", new Object[]{responseCode, responseGib});
            return null;
        }
    }

    public HashMap<String, byte[]> getInvoiceFile(boolean isOutbox, String fileType, String uuid) throws CustomException {
        HashMap<String, byte[]> fileMap = new HashMap<>();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", uuid);
        parameters.put("action", fileType);
        String serviceUrl;
        if (fileType.equals("ARCHIVE")) {
            serviceUrl = archiveUrl + "/envelope";
        } else {
            if (isOutbox) {
                serviceUrl = outboxUrl + "/action";
            } else {
                serviceUrl = inboxUrl + "/action";
            }
        }
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, token);
        try {
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                byte[] responsebyte = IOUtils.toByteArray(con.getInputStream());
                String fileDisposition = con.getHeaderField("Content-Disposition");
                if (fileDisposition != null && fileDisposition.contains("=")) {
                    String fileName = fileDisposition.split("=")[1]; //getting value after '='
                    fileMap.put(fileName, responsebyte);
                } else {
                    throw new WebApplicationException("Dosya ismi alınamadı", Response.Status.INTERNAL_SERVER_ERROR);
                }
            } else {
                byte[] responsebyte = IOUtils.toByteArray(con.getErrorStream());
                String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
                ResponseData<Response> errorResponse = new Gson().fromJson(responseGib, new TypeToken<ResponseData<Response>>() {
                }.getType());
                throw new CustomException(errorResponse.getError().getMessage());
            }
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return fileMap;
    }

    public ResponseData<String> actionInvoice(String uuid, String action, String description) throws CustomException {
        ResponseData<String> response = new ResponseData<>();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", uuid);
        parameters.put("action", action);
        parameters.put("description", description);
        String serviceUrl;
        if (action.equals("DELETE")) {
            serviceUrl = outboxUrl;
        } else {
            serviceUrl = inboxUrl;
        }
        serviceUrl += "/action";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, token);
        try {
            int responseCode = con.getResponseCode();
            byte[] responsebyte;
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            response = new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public List<GibUser> getGibUsers(String vknNumber, String aliasType) {
        String response = null;
        int responseCode = 0;
        try {
            URL obj = new URL(userUrl + "?id=" + vknNumber + "&aliasType=" + aliasType);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", token);
            responseCode = con.getResponseCode();
            byte[] responsebyte = IOUtils.toByteArray(con.getInputStream());
            response = new String(responsebyte, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        ResponseData<List<GibUser>> gibUsersResponse = null;
        if (responseCode == 200) {
            gibUsersResponse = gson.fromJson(response, new TypeToken<ResponseData<List<GibUser>>>() {
            }.getType());
            return gibUsersResponse.getData();
        } else {
            return null;
        }
    }

    public ResponseData<String> sendInvoice(List<Invoice> invoices) throws CustomException {
        ResponseData<String> response = new ResponseData<>();
        int responseCode = 0;
        String responseGib = "";
        InvoiceDao invoicedao = new InvoiceDao();
        for (int i = 0; i < invoices.size(); i++) {
            invoices.set(i, invoicedao.getInvoicesJson(invoices.get(i).getId(), null, authTenant.getId(), null, null, true, null, null, null, 1, 0).get(0));
            if (((invoices.get(i).getReceiverIdentifier() == null || invoices.get(i).getReceiverIdentifier().equals(""))
                    && !invoices.get(i).getInvoiceScenario().getCode().equals("EARSIVFATURA"))) {
                throw new CustomException("Müşteri e-fatura kullanıcısı değil");
            }
        }
        for (int i = 0; i < invoices.size(); i++) {
            InvoiceXml invoiceXml = new InvoiceXml();
            String xmlString = invoiceXml.getInvoiceXml(invoices.get(i));
            byte[] xmlByte = (xmlString.getBytes(StandardCharsets.UTF_8));

            try {
                CloseableHttpClient httpClient = HttpClients.createDefault();

                HttpPost uploadFile = new HttpPost(outboxUrl);
                uploadFile.addHeader("Authorization", token);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                if (invoices.get(i).getReceiverIdentifier() != null) {
                    builder.addTextBody("receiverPostBox", invoices.get(i).getReceiverIdentifier(), ContentType.TEXT_PLAIN);
                }
                // This attaches the file to the POST:
                //File f = new File("[/path/to/upload]");
                builder.addBinaryBody(
                        "file",
                        xmlByte,
                        ContentType.APPLICATION_OCTET_STREAM,
                        "fatura.xml"
                );

                HttpEntity multipart = builder.build();
                uploadFile.setEntity(multipart);
                CloseableHttpResponse httpResponse = httpClient.execute(uploadFile);
                responseCode = httpResponse.getStatusLine().getStatusCode();
                responseGib = EntityUtils.toString(httpResponse.getEntity());
                if (responseCode == 200) {
                    if (invoices.get(i).getInvoiceScenario().getCode().equals("EARSIVFATURA")) {
                        response.succeed("Fatura e-Arşiv raporu için gönderildi.");
                    } else {
                        response.succeed("e-Fatura Gelirler İdaresi Başkanlığı'na gönderildi.");
                    }
                } else {
                    response = new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
                    }.getType());
                }
            } catch (IOException e) {
                throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }

    public ResponseData<String> integrateUser(Integration integration) throws CustomException {
        ResponseData<String> response;
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("code", integration.getCode());
        parameters.put("password", integration.getPassword());
        parameters.put("partyId", integration.getPartyId());
        parameters.put("partyName", integration.getPartyName());
        parameters.put("firstName", integration.getFirstName());
        parameters.put("middleName=", integration.getMiddleName());
        parameters.put("lastName", integration.getLastName());
        parameters.put("accountCodeList", integration.getAccountCodeList());
        parameters.put("pk", integration.getPk());
        parameters.put("gb", integration.getGb());
        parameters.put("certificateSerialNumber", integration.getCertificateSerialNumber());
        parameters.put("pin", integration.getPin());
        String serviceUrl = userUrl + "/integration";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, "");
        try {
            int responseCode = con.getResponseCode();
            byte[] responsebyte;
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            response = new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public Dashboard getDashboard(User authUser) {
        Tenant tenant = authUser.getTenantUsers().getTenant();
        Dashboard dashboard = new Dashboard();
        InvoiceDao invoiceDao = new InvoiceDao();
        //List<GibInbox> gibInbox = getInboxInvoices(ticket, null, null);
        //List<GibInbox> gibOutbox = getOutboxInvoices(ticket, null, null);
        List<Invoice> erpInbox = invoiceDao.getInvoicesJson(0, null, tenant.getId(), null, "true", true, null, null, null, 9999999, 0);
        List<Invoice> erpOutbox = invoiceDao.getInvoicesJson(0, null, tenant.getId(), null, "false", true, null, null, null, 9999999, 0);
        dashboard.setInbox(erpInbox);
        dashboard.setOutbox(erpOutbox);
        /*if (gibInbox!=null) {
            dashboard.setInboxCount(gibInbox.size());
        }
        if (gibOutbox!=null) {
            dashboard.setOutboxCount(gibOutbox.size());
        }*/
        Date expireDate = new Date();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            expireDate = df.parse(tenant.getExpireDate());
        } catch (ParseException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        long diff = expireDate.getTime() - new Date().getTime();
        dashboard.setRemainingExpireDate(diff / (1000 * 60 * 60 * 24));

        CustomerDao customerdao = new CustomerDao();
        List<Customer> customers = customerdao.getCustomers(tenant.getId(), 0, null, null, 0, null, null, null, null, 999999999, 0);
        dashboard.setCustomerCount(customers.size());

        ItemDao itemdao = new ItemDao();
        Item item = new Item();
        item.setTenantId(tenant.getId());
        List<Item> items = itemdao.getItems(item, null, null, "i.stock desc", 999999999, 0);
        dashboard.setItem(items);
        dashboard.setItemCount(items.size());

        if (authUser.getTenantUsers().getUserRole().isIsEinvoiceAdmin()
                || authUser.getTenantUsers().getTenant().getTenantInfo().isIsEfaturaUser()) {
            dashboard.setStatistic(getEinvoiceUser().getData().getStatistic());
        }

        return dashboard;
    }

    public ResponseData<String> sendCreditRequest(long amount, String descriptin) {
        ResponseData<String> response = new ResponseData<>();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("amount", amount);
        parameters.put("desription", descriptin);
        String serviceUrl = userUrl + "/creditRequest";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, token);
        try {
            byte[] responsebyte;
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            response = new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public ResponseData<List<EInvoiceCreditRequest>> getCreditRequest(long id, String vkn, String name, String confirmed, String isAdmin, long limit, long offset) {
        String parameters = "";
        if (vkn != null && !vkn.equals("")) {
            parameters += "&vkn=" + vkn;
        }
        if (name != null && !name.equals("")) {
            parameters += "&name=" + name;
        }
        if (id != 0) {
            parameters += "&id=" + id;
        }
        if (confirmed != null && !confirmed.equals("")) {
            parameters += "&confirmed=" + confirmed;
        }
        if (isAdmin != null && (isAdmin.equals("true") || isAdmin.equals("false"))) {
            parameters += "&isAdmin=" + isAdmin;
        }
        try {
            URL obj = new URL(adminUrl + "/creditRequest?limit=" + limit + "&offset=" + offset + parameters);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", token);
            int responseCode = con.getResponseCode();
            byte[] responsebyte;
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            return new Gson().fromJson(responseGib, new TypeToken<ResponseData<List<EInvoiceCreditRequest>>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseData<String> acceptCreditRequest(long id) {
        ResponseData<String> response = new ResponseData<>();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        String serviceUrl = adminUrl + "/acceptCreditRequest";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, token);
        try {
            byte[] responsebyte;
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            response = new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public ResponseData<String> sendCredit(String vkn, long amount) {
        ResponseData<String> response = new ResponseData<>();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("vkn", vkn);
        parameters.put("amount", amount);
        String serviceUrl = adminUrl + "/addMukellefCredit";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, token);
        try {
            byte[] responsebyte;
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            response = new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public ResponseData<List<EInvoiceUser>> getEInviceUsers(long id, String vkn, String name, String isAdmin, long limit, long offset) {
        String parameters = "";
        if (id != 0) {
            parameters += "&id=" + id;
        }
        if (vkn != null && !vkn.equals("")) {
            parameters += "&vkn=" + vkn;
        }
        if (name != null && !name.equals("")) {
            parameters += "&name=" + name;
        }
        if (isAdmin != null && (isAdmin.equals("true") || isAdmin.equals("false"))) {
            parameters += "&isAdmin=" + isAdmin;
        }
        try {
            URL obj = new URL(adminUrl + "/getUsers?limit=" + limit + "&offset=" + offset + parameters);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", token);
            int responseCode = con.getResponseCode();
            byte[] responsebyte;
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            return new Gson().fromJson(responseGib, new TypeToken<ResponseData<List<EInvoiceUser>>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseData<String> registerEinvoiceAdmin(String name, String email) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("email", email);
        String serviceUrl = adminUrl + "/registerAdmin";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, token);
        try {
            byte[] responsebyte;
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            return new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseData<String> confirmEinvoiceAdmin(String code, String password) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("code", code);
        parameters.put("password", password);
        String serviceUrl = adminUrl + "/confirmAdmin";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, "");
        try {
            byte[] responsebyte;
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            return new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseData<String> registerEinvoiceUser(String vkn, String email) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("vkn", vkn);
        parameters.put("email", email);
        String serviceUrl = adminUrl + "/registerUser";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, token);
        try {
            byte[] responsebyte;
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            return new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseData<EInvoiceUser> getEinvoiceUser() {
        try {
            URL obj = new URL(userUrl + "/information");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", token);
            int responseCode = con.getResponseCode();
            byte[] responsebyte;
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            return new Gson().fromJson(responseGib, new TypeToken<ResponseData<EInvoiceUser>>() {
            }.getType());
        } catch (JsonSyntaxException | IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseData<List<EInvoiceReport>> getReport(String vkn, String startDate, String endDate, long limit, long offset) {
        String parameters = "";
        if (vkn != null && !vkn.equals("")) {
            parameters += "&vkn=" + vkn;
        }
        if (startDate != null && endDate != null && !startDate.equals("") && !endDate.equals("")) {
            parameters += "&startDate=" + startDate + "&endDate=" + endDate;
        }
        try {
            URL obj = new URL(archiveUrl + "/reports" + "?limit=" + limit + "&offset=" + offset + parameters);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", token);
            int responseCode = con.getResponseCode();
            byte[] responsebyte;
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            return new Gson().fromJson(responseGib, new TypeToken<ResponseData<List<EInvoiceReport>>>() {
            }.getType());
        } catch (JsonSyntaxException | IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseData<EInvoiceReport> createReport(String vkn, String startDate, String endDate) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("vkn", vkn);
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        String serviceUrl = archiveUrl + "/createReport";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, token);
        try {
            byte[] responsebyte;
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            return new Gson().fromJson(responseGib, new TypeToken<ResponseData<EInvoiceReport>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseData<String> sendReport(String id) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        String serviceUrl = archiveUrl + "/sendReport";
        HttpURLConnection con = new HttpUtilities().postService(parameters, serviceUrl, token);
        try {
            byte[] responsebyte;
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                responsebyte = IOUtils.toByteArray(con.getInputStream());
            } else {
                responsebyte = IOUtils.toByteArray(con.getErrorStream());
            }
            String responseGib = new String(responsebyte, StandardCharsets.UTF_8);
            return new Gson().fromJson(responseGib, new TypeToken<ResponseData<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
