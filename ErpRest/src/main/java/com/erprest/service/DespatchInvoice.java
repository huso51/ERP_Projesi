/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import com.erprest.model.Invoice;
import com.erprest.model.InvoiceLine;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import tr.gov.efatura.xjc.*;

/**
 *
 * @author msi_ge72
 */
public class DespatchInvoice {

    private static final Logger logger = Logger.getLogger(DespatchInvoice.class.getName());

    public String getXml(Invoice invoice) {
        JaxBUtility jaxBUtility = JaxBUtility.getInstance(DespatchAdviceType.class); 
        DespatchAdviceType despatch = (DespatchAdviceType)jaxBUtility.unmarshallSample();
        XmlCommons xmlCommons = new XmlCommons();
        XMLGregorianCalendar issueDate, issueTime, orderDate = null;
        try {
            issueDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(invoice.getCreatedAt().split("\\s+")[0]);
            issueTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(invoice.getCreatedAt().split("\\s+")[1]);
            if (invoice.getOrderNo() != null && !invoice.getOrderNo().equals("")) {
                orderDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(invoice.getOrderDate());
            }
        } catch (DatatypeConfigurationException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }

        ObjectFactory factory = new ObjectFactory();
        despatch.getID().setValue(invoice.getSn1() + invoice.getSn2() + invoice.getSn3());
        despatch.getUUID().setValue(invoice.getUuid());
        despatch.getIssueDate().setValue(issueDate);
        despatch.getIssueTime().setValue(issueTime);
        if (invoice.getNote() != null && !invoice.getNote().equals("")) {
            String noteLine[] = invoice.getNote().split("\\r?\\n");
            for (String note : noteLine) {
                NoteType noteType = factory.createNoteType();
                noteType.setValue(note);
                despatch.getNote().add(noteType);
            }
        }
        if (invoice.getOrderNo() != null && !invoice.getOrderNo().equals("")) {
            despatch.getOrderReference().get(0).setID(factory.createIDType());
            despatch.getOrderReference().get(0).getID().setValue(invoice.getOrderNo());
            despatch.getOrderReference().get(0).setIssueDate(factory.createIssueDateType());
            despatch.getOrderReference().get(0).getIssueDate().setValue(orderDate);
        }
        despatch.getAdditionalDocumentReference().get(0).getID().setValue(invoice.getUuid());
        despatch.getAdditionalDocumentReference().get(0).getIssueDate().setValue(issueDate);
        despatch.getAdditionalDocumentReference().get(0).getAttachment().getEmbeddedDocumentBinaryObject().setFilename(
                invoice.getSn1() + invoice.getSn2() + invoice.getSn3() + ".xslt");
        try {
            despatch.getAdditionalDocumentReference().get(0).getAttachment().getEmbeddedDocumentBinaryObject().setValue(
                    new InvoiceXslt().getInvoiceXslt(
                            invoice.getInvoiceScenario().getCode(),
                            invoice.getTenant().getLogo(),
                            invoice.getTenant().getSignature()).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, null, e);
        }

        despatch.getSignature().get(0).getID().setValue(invoice.getTenant().getTenantInfo().getTc());
        despatch.getSignature().get(0).setSignatoryParty(
                xmlCommons.getPartyElement(despatch.getSignature().get(0).getSignatoryParty(), invoice.getTenant().getTenantInfo()));
        despatch.getDespatchSupplierParty().setParty(
                xmlCommons.getPartyElement(despatch.getDespatchSupplierParty().getParty(), invoice.getTenant().getTenantInfo()));
        despatch.getDeliveryCustomerParty().setParty(
                xmlCommons.getPartyElement(despatch.getDeliveryCustomerParty().getParty(), invoice.getCustomer()));

        despatch.getShipment().getGoodsItem().get(0).getValueAmount().setCurrencyID(invoice.getCurrency().getCode());
        despatch.getShipment().getGoodsItem().get(0).getValueAmount().setValue(new BigDecimal("" + invoice.getPriceTotal()));

        for (int i = 0; i < invoice.getInvoiceLine().size(); i++) {
            InvoiceLine line = invoice.getInvoiceLine().get(i);
            DespatchLineType despatchLine = despatch.getDespatchLine().get(0);
            despatch.getDespatchLine().set(i, despatchLine);
            despatch.getDespatchLine().get(i).getID().setValue("" + (i + 1));
            despatch.getDespatchLine().get(i).getDeliveredQuantity().setUnitCode(line.getItem().getUomCode().getCode());
            despatch.getDespatchLine().get(i).getDeliveredQuantity().setValue(new BigDecimal("" + line.getQuantity()));
            despatch.getDespatchLine().get(i).getOrderLineReference().getLineID().setValue("" + (i + 1));
            despatch.getDespatchLine().get(i).getItem().getName().setValue(line.getItem().getName());

            InvoiceLineType lineType = despatch.getDespatchLine().get(i).getShipment().get(0).getGoodsItem().get(0).getInvoiceLine().get(0);
            lineType.getInvoicedQuantity().setUnitCode(despatch.getDespatchLine().get(i).getDeliveredQuantity().getUnitCode());
            lineType.getInvoicedQuantity().setValue(despatch.getDespatchLine().get(i).getDeliveredQuantity().getValue());
            lineType.getLineExtensionAmount().setCurrencyID(invoice.getCurrency().getCode());
            lineType.getLineExtensionAmount().setValue(new BigDecimal("" + line.getGrossTotal()));
            lineType.getPrice().getPriceAmount().setCurrencyID(invoice.getCurrency().getCode());
            lineType.getPrice().getPriceAmount().setValue(new BigDecimal("" + line.getPrice()));
            despatch.getDespatchLine().get(i).getShipment().get(0).getGoodsItem().get(0).getInvoiceLine().set(0, lineType);
        }
        return jaxBUtility.marshallSample(despatch);
    }
}
