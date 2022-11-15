/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.dao;

import com.erprest.connection.DBConnectionHelper;
import com.erprest.connection.EMailConnectionHelper;
import com.erprest.filter.CustomException;
import com.erprest.model.CustomerPayment;
import com.erprest.model.EmailConnection;
import com.erprest.model.Invoice;
import com.erprest.model.InvoiceException;
import com.erprest.model.InvoiceLine;
import com.erprest.model.InvoiceLineTaxes;
import com.erprest.model.InvoiceScenario;
import com.erprest.model.InvoiceWithholding;
import com.erprest.model.InvoiceType;
import com.erprest.model.Item;
import com.erprest.model.ItemTaxes;
import com.erprest.model.PaymentCash;
import com.erprest.model.TaxesType;
import com.erprest.model.Tenant;
import com.erprest.model.User;
import com.erprest.service.Calculates;
import com.erprest.service.FileSystem;
import com.erprest.service.InvoiceXml;
import com.erprest.service.InvoiceXslt;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author msi_ge72
 */
public class InvoiceDao {

    public void addInvoice(User authUser, Invoice invoice) throws CustomException {
        Tenant authTenant=authUser.getTenantUsers().getTenant();
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        Gson gson = new Gson();
        Calculates calc = new Calculates();
        invoice.setTenant(authTenant);
        invoice = calc.CalculateInvoice(invoice);
        try {
            conn = dbhelper.getConnection();
            invoice.setUuid(UUID.randomUUID().toString());

            conn.setAutoCommit(false);
            int psindex = 1;
            ps = conn.prepareStatement("insert into invoice (uuid,customer_id,tenant_id,is_coming_invoice,invoice_scenario_id,invoice_type_id,sn1,sn2,sn3,currency_id,order_no,waybill_number,price_total,receiver_identifier,order_date,waybill_date,note,spending_type_id,created_at)\n"
                    + "                         values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(psindex++, invoice.getUuid());
            ps.setLong(psindex++, invoice.getCustomer().getId());
            ps.setString(psindex++, invoice.getTenant().getId());
            ps.setBoolean(psindex++, invoice.isIsComingInvoice());
            ps.setLong(psindex++, invoice.getInvoiceScenario().getId());
            ps.setLong(psindex++, invoice.getInvoiceType().getId());
            ps.setString(psindex++, invoice.getSn1());
            ps.setString(psindex++, invoice.getSn2());
            ps.setString(psindex++, invoice.getSn3());
            ps.setLong(psindex++, invoice.getCurrency().getId());
            ps.setString(psindex++, invoice.getOrderNo());
            ps.setString(psindex++, invoice.getWaybillNumber());
            ps.setDouble(psindex++, invoice.getPriceTotal());
            ps.setString(psindex++, invoice.getReceiverIdentifier());
            ps.setString(psindex++, invoice.getOrderDate());
            ps.setString(psindex++, invoice.getWaybillDate());
            ps.setString(psindex++, invoice.getNote());
            ps.setLong(psindex++, invoice.getSpendingType() != null ? invoice.getSpendingType().getId() : 0);
            ps.setString(psindex++, invoice.getCreatedAt());
            ps.executeUpdate();

            ResultSet keys1 = ps.getGeneratedKeys();
            keys1.next();
            invoice.setId(keys1.getInt(1));

            if (invoice.getInvoiceWithholding() != null) {
                ps = conn.prepareStatement("update invoice set invoice_withholding_id=? where id=?");
                ps.setLong(1, invoice.getInvoiceWithholding().getId());
                ps.setLong(2, invoice.getId());
                ps.executeUpdate();
            }

            if (invoice.getInvoiceException() != null) {
                ps = conn.prepareStatement("update invoice set invoice_exception_id=? where id=?");
                ps.setLong(1, invoice.getInvoiceException().getId());
                ps.setLong(2, invoice.getId());
                ps.executeUpdate();
            }

            for (int i = 0; i < invoice.getInvoiceLine().size(); i++) {
                List<ItemTaxes> itemTexas = invoice.getInvoiceLine().get(i).getItem().getItemTaxes();
                for (int j = 0; j < itemTexas.size(); j++) {
                    InvoiceLineTaxes lineTaxes = new InvoiceLineTaxes();
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().add(lineTaxes);
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().get(j).setInvoiceId(invoice.getId());
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().get(j).setValue(itemTexas.get(j).getValue());
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().get(j).setInvoiceLineId(invoice.getInvoiceLine().get(i).getId());
                    TaxesType taxesType = new TaxesType();
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().get(j).setTaxesType(taxesType);
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().get(j).getTaxesType().setId(itemTexas.get(j).getTaxesType().getId());
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().get(j).setAmount(itemTexas.get(j).getTaxesType().getTaxAmount());
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().get(j).getTaxesType().setId(itemTexas.get(j).getTaxesType().getId());
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().get(j).getTaxesType().setCode(itemTexas.get(j).getTaxesType().getCode());
                    invoice.getInvoiceLine().get(i).getInvoiceLineTaxes().get(j).getTaxesType().setDescription(itemTexas.get(j).getTaxesType().getDescription());
                }
                ItemDao itemDao = new ItemDao();
                Item item = itemDao.getItems(invoice.getInvoiceLine().get(i).getItem(), null, null, null, 1, 0).get(0); //Güncel item bilgisi
                if (invoice.getInvoiceType().getCode().equals("IADE") || invoice.isIsComingInvoice()) {
                    itemDao.addStock(item.getId(), invoice.getInvoiceLine().get(i).getQuantity());
                } else {
                    if (item.getStock() < invoice.getInvoiceLine().get(i).getQuantity()) {
                        throw new CustomException(invoice.getInvoiceLine().get(i).getItem().getName() + " : Yetersiz stok");
                    } else {
                        itemDao.addStock(item.getId(), -invoice.getInvoiceLine().get(i).getQuantity());
                    }
                }
            }
            invoice.getTenant().setTenantInfo(authTenant.getTenantInfo());
            if (invoice.getCreatedAt() == null || invoice.getCreatedAt().equals("")) {
                Date now = new Date();
                invoice.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
            }
            ps = conn.prepareStatement("update invoice set invoice_json=? where id=?");
            ps.setString(1, gson.toJson(invoice));
            ps.setLong(2, invoice.getId());
            ps.executeUpdate();
            conn.commit();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        CustomerPayment payment=new CustomerPayment();
        payment.setCustomer(invoice.getCustomer());
        payment.setIsPaid(false);
        //PaymentCash paymentCash = new PaymentCash();
        if(invoice.isIsComingInvoice()){
            payment.setReceivable(invoice.getPriceTotal());
            //paymentCash.setIsPayingToCustomer(true);
        }else{
            payment.setDebt(invoice.getPriceTotal());
            //paymentCash.setIsPayingToCustomer(false);
        }
        
        /*paymentCash.setAmount(invoice.getPriceTotal());
        paymentCash.setDescription(invoice.getNote());
        paymentCash.setTenantAccount();
        payment.setPaymentCash(paymentCash);*/
        
        payment.setType("");
        payment.setTenant(authTenant);
        new AccountDao().addCustomerPayment(payment, authUser);
    }

    public List<InvoiceType> getInvoiceTypes() {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<InvoiceType> types = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from invoice_type");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InvoiceType invoiceType = new InvoiceType();
                invoiceType.setId(rs.getLong("id"));
                invoiceType.setCode(rs.getString("code"));
                invoiceType.setDescription(rs.getString("description"));
                types.add(invoiceType);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return types;
    }

    public List<InvoiceWithholding> getTevkifatTypes() {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<InvoiceWithholding> types = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from invoice_withholding");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InvoiceWithholding tevkifatType = new InvoiceWithholding();
                tevkifatType.setId(rs.getLong("id"));
                tevkifatType.setCode(rs.getString("code"));
                tevkifatType.setValue(rs.getLong("value"));
                tevkifatType.setDescription(rs.getString("description"));
                types.add(tevkifatType);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return types;
    }

    public List<InvoiceException> getExceptionTypes() {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<InvoiceException> types = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from invoice_exception");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InvoiceException exceptionType = new InvoiceException();
                exceptionType.setId(rs.getLong("id"));
                exceptionType.setCode(rs.getString("code"));
                exceptionType.setDescription(rs.getString("description"));
                types.add(exceptionType);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return types;
    }

    public String getInvoiceHtml(long invoiceId, String tenantId) {
        InvoiceXml invoiceXml = new InvoiceXml();
        InvoiceXslt invoiceXslt = new InvoiceXslt();
        String invoiceHtml;

        Invoice invoice = getInvoicesJson(invoiceId, null, tenantId, null, null, true, null, null, null, 1, 0).get(0);
        try {
            String xmlString = invoiceXml.getInvoiceXml(invoice);
            String xsltString = invoiceXslt.getInvoiceXslt(
                    invoice.getInvoiceScenario().getCode(),
                    invoice.getTenant().getLogo(),
                    invoice.getTenant().getSignature());

            ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
            Result result = new StreamResult(outputStream2);

            Source xsltFile = new StreamSource(new StringReader(xsltString));
            Source xmlFile = new StreamSource(new StringReader(xmlString));

            Templates template = TransformerFactory.newInstance().newTemplates(xsltFile);
            Transformer transformer2 = template.newTransformer();
            transformer2.transform(xmlFile, result);

            invoiceHtml = outputStream2.toString("UTF-8");
        } catch (TransformerException | UnsupportedEncodingException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return invoiceHtml;
    }

    public HashMap<String, byte[]> getInvoicePdf(List<Invoice> invoices, String tenantId) {
        HashMap<String, byte[]> pdfFile = new HashMap<>();
        FileSystem fileSystem = new FileSystem();
        String osType = System.getProperty("os.name");
        String wkTempDir = "", wkhtmlName = "";
        if (osType.toLowerCase().contains("windows")) {
            wkTempDir = "C:/Users/msi_ge72/Desktop/invoiceTemp/";
            wkhtmlName = "wkhtmltopdf.exe";
        } else if (osType.toLowerCase().contains("linux")) {
            wkTempDir = fileSystem.invoiceTemp;
            wkhtmlName = fileSystem.wkhtmltopdf;
        }

        String zipFilePath = wkTempDir + UUID.randomUUID().toString() + ".zip";
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFilePath);
            zos = new ZipOutputStream(fos);

            for (int i = 0; i < invoices.size(); i++) {
                String html = getInvoiceHtml(invoices.get(i).getId(), tenantId);
                String pdfFilePath, fileName, htmlFilePath;
                invoices.set(i, getInvoicesJson(invoices.get(i).getId(), null, tenantId, null, null, true, null, null, null, 1, 0).get(0));

                fileName = invoices.get(i).getSn1() + invoices.get(i).getSn2() + invoices.get(i).getSn3();

                htmlFilePath = wkTempDir + fileName + ".html";
                pdfFilePath = wkTempDir + fileName + ".pdf";

                PrintWriter out = new PrintWriter(htmlFilePath);
                out.println(html);
                out.close();
                if (osType.toLowerCase().contains("linux")) {
                    fileSystem.addFilePermissions(htmlFilePath);
                }
                Runtime rt = Runtime.getRuntime();
                if (osType.toLowerCase().contains("windows")) {
                    /*System.out.println("wkhtml log: " + wkTempDir + wkhtmlName + " "
                            + "file:///" + htmlFilePath + " "
                            + pdfFilePath);*/
                    Process pr = rt.exec(wkTempDir + wkhtmlName + " "
                            + "file:///" + htmlFilePath + " "
                            + pdfFilePath);
                    int exitCode = pr.waitFor();
                } else if (osType.toLowerCase().contains("linux")) {
                    /*System.out.println("wkhtml log: " + wkhtmlName + " "
                            + htmlFilePath + " "
                            + pdfFilePath);*/
                    Process pr = rt.exec(wkhtmlName + " "
                            + htmlFilePath + " "
                            + pdfFilePath);
                    int exitCode = pr.waitFor();
                }

                if (osType.toLowerCase().contains("linux")) {
                    fileSystem.addFilePermissions(pdfFilePath);
                }

                zos.putNextEntry(new ZipEntry(new File(pdfFilePath).getName()));

                byte[] bytes = Files.readAllBytes(Paths.get(pdfFilePath));
                zos.write(bytes, 0, bytes.length);
                zos.closeEntry();

                new File(htmlFilePath).delete();
                new File(pdfFilePath).delete();
            }
            zos.close();
            if (osType.toLowerCase().contains("linux")) {
                fileSystem.addFilePermissions(zipFilePath);
            }
            byte[] pdfByte = Files.readAllBytes(new File(zipFilePath).toPath());
            pdfFile.put("pdfFile.zip", pdfByte);
        } catch (IOException | InterruptedException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        new File(zipFilePath).delete();
        return pdfFile;
    }

    public void sendToCustomer(List<Invoice> invoices, String tenantId) {
        for (int i = 0; i < invoices.size(); i++) {
            if (invoices.get(i) != null) {
                List<Invoice> newList = new ArrayList<>();
                newList.add(invoices.get(i));
                for (int j = i; j < invoices.size(); j++) {
                    if (invoices.get(i).getCustomer().getId() == invoices.get(j).getCustomer().getId() && i != j) {
                        newList.add(invoices.get(j));
                        invoices.set(j, null);
                    }
                }
                HashMap<String, byte[]> zippedPdf = getInvoicePdf(newList, tenantId);
                Map.Entry<String, byte[]> mentry = (Map.Entry) zippedPdf.entrySet().iterator().next();
                TenantDao tenantdao = new TenantDao();
                EmailConnection emailConnection = tenantdao.emailSetting(tenantId);
                EMailConnectionHelper econn = new EMailConnectionHelper(emailConnection);
                econn.sendtoCustomer(invoices.get(i).getTenant().getName(), invoices.get(i).getCustomer().getAddress().get(0).getEmail(), mentry.getValue());
            }
        }
    }

    public void deleteInvoice(List<Invoice> invoices, String tenantId) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            conn.setAutoCommit(false);
            ItemDao itemDao = new ItemDao();
            //Herbir faturayı pasif olarak işaretle
            for (Invoice invoice : invoices) {
                invoice = getInvoicesJson(invoice.getId(), null, null, null, null, true, null, null, null, 1, 0).get(0);
                ps = conn.prepareStatement("update invoice set confirmed=0 where id=? and tenant_id=?");
                ps.setLong(1, invoice.getId());
                ps.setString(2, tenantId);
                ps.executeUpdate();
                //iptal edilen faturadaki herbir satırındaki ürünler stoğa iadesi
                for (InvoiceLine invoiceLine : invoice.getInvoiceLine()) {
                    itemDao.addStock(invoiceLine.getItem().getId(), invoiceLine.getQuantity());
                }
            }
            conn.commit();
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<InvoiceScenario> getInvoiceScenario() {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<InvoiceScenario> types = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from invoice_scenario");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InvoiceScenario invoiceScenario = new InvoiceScenario();
                invoiceScenario.setId(rs.getLong("id"));
                invoiceScenario.setCode(rs.getString("code"));
                invoiceScenario.setDescription(rs.getString("description"));
                types.add(invoiceScenario);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return types;
    }

    public List<Invoice> getInvoicesJson(long invoiceId, String invoiceNo, String tenantId, String customerfilter, String isComingInvoice, boolean confirmed, String whereDateAfter, String whereDateBefore, String order_by, long limit, long offset) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        String whereParameter, temp = "";
        ArrayList paramList = new ArrayList();

        temp += " and iv.confirmed=?";
        paramList.add(confirmed);
        if (customerfilter != null && !customerfilter.equals("")) {
            temp += " and c.name like ?";
            paramList.add("%" + customerfilter + "%");
        }
        if (invoiceId != 0) {
            temp += " and iv.id=?";
            paramList.add(invoiceId);
        }
        if (invoiceNo != null && !invoiceNo.equals("")) {
            temp += " and concat(iv.sn1,iv.sn2,iv.sn3)=?";
            paramList.add(invoiceNo);
        }
        if (tenantId != null && !tenantId.equals("")) {
            temp += " and iv.tenant_id= ?";
            paramList.add(tenantId);
        }
        if (isComingInvoice != null && (isComingInvoice.equals("true") || isComingInvoice.equals("false"))) {
            temp += " and iv.is_coming_invoice= ?";
            paramList.add(Boolean.parseBoolean(isComingInvoice));
        }
        if (whereDateAfter != null && !whereDateAfter.equals("")) {
            temp += " and iv.created_at > ?";
            paramList.add(whereDateAfter);
        }
        if (whereDateBefore != null && !whereDateBefore.equals("")) {
            temp += " and iv.created_at < ?";
            paramList.add(whereDateBefore);
        }
        if (order_by == null) {
            order_by = "";
        } else {
            order_by = " order by " + order_by;
        }
        whereParameter = temp;
        List<Invoice> Invoices = new ArrayList<>();

        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select iv.*,c.name from invoice iv \n"
                    + "                     left join customers c on c.id=iv.customer_id\n "
                    + "                         where 1=1 " + whereParameter + order_by + " limit " + limit + " offset " + offset);

            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gson gson = new Gson();
                Invoice invoice = gson.fromJson(rs.getString("invoice_json"), Invoice.class);
                Invoices.add(invoice);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return Invoices;
    }

    public String getIncrementSn3(String tenantId, String sn1, String sn2) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        String sn3 = "";
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select sn3 from invoice "
                    + "where tenant_id=? and sn1=? and sn2=? and confirmed=true order by id desc limit 1");
            ps.setString(1, tenantId);
            ps.setString(2, sn1);
            ps.setString(3, sn2);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sn3 = String.format("%09d", Long.parseLong(rs.getString("sn3")) + 1);
            }
            if (sn3 == null || sn3.equals("")) {
                sn3 = (String.format("%09d", 1));
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return sn3;
    }

}
