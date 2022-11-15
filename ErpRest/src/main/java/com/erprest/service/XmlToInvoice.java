/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import com.erprest.filter.CustomException;
import com.erprest.model.Address;
import com.erprest.model.City;
import com.erprest.model.Currency;
import com.erprest.model.Customer;
import com.erprest.model.District;
import com.erprest.model.Invoice;
import com.erprest.model.InvoiceLine;
import com.erprest.model.InvoiceScenario;
import com.erprest.model.InvoiceTaxes;
import com.erprest.model.InvoiceType;
import com.erprest.model.Item;
import com.erprest.model.TaxesType;
import com.erprest.model.Tenant;
import com.erprest.model.UomCode;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author msi_ge72
 */
public class XmlToInvoice {

    private static final Logger logger = Logger.getLogger(XmlToInvoice.class.getName());

    public Invoice getInvoice(Tenant authTenant, String invoiceXml) throws CustomException {
        Invoice invoice = new Invoice();
        Customer customer = new Customer();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder dbuilder = dbf.newDocumentBuilder();
            Document document = dbuilder.parse(new ByteArrayInputStream(invoiceXml.getBytes("UTF-8")));
            XLookup lookup = new XLookup(document);
            lookup.setNamespace("inv", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
            lookup.setNamespace("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
            lookup.setNamespace("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");

            String profileId = lookup.getNode("//inv:Invoice/cbc:ProfileID").getTextContent();
            invoice.setInvoiceScenario(InvoiceScenario.byCode(profileId));
            String id = lookup.getNode("//inv:Invoice/cbc:ID").getTextContent();
            invoice.setSn1(id.substring(0, 3));
            invoice.setSn2(id.substring(3, 7));
            invoice.setSn3(id.substring(7, id.length()));

            invoice.setUuid(lookup.getNode("//inv:Invoice/cbc:UUID").getTextContent());
            String invoiceType = lookup.getNode("//inv:Invoice/cbc:InvoiceTypeCode").getTextContent();
            invoice.setInvoiceType(InvoiceType.byCode(invoiceType));

            NodeList noteList = lookup.getNodeList("//inv:Invoice/cbc:Note");
            if (noteList.getLength() > 0) {
                invoice.setNote("");
                for (int i = 0; i < noteList.getLength(); i++) {
                    invoice.setNote(invoice.getNote() + noteList.item(i).getTextContent() + "\n");
                }
            }

            invoice.setCurrency(Currency.byCode(lookup.getNode("//inv:Invoice/cbc:DocumentCurrencyCode").getTextContent()));
            Node orderReferance = lookup.getNode("//inv:Invoice/cac:OrderReference");
            if (orderReferance != null) {
                invoice.setOrderNo(lookup.getNode("//inv:Invoice/cac:OrderReference/cbc:Id").getTextContent());
                invoice.setOrderDate(lookup.getNode("//inv:Invoice/cac:OrderReference/cbc:IssueDate").getTextContent() + " 00:00:00");
            }
            Node despatchReference = lookup.getNode("//inv:Invoice/cac:DespatchDocumentReference");
            if (despatchReference != null) {
                invoice.setWaybillNumber(lookup.getNode("//inv:Invoice/cac:DespatchDocumentReference/cbc:Id").getTextContent());
                invoice.setWaybillDate(lookup.getNode("//inv:Invoice/cac:DespatchDocumentReference/cbc:IssueDate").getTextContent() + " 00:00:00");
            }
            String supplierParty = lookup.getNode("//inv:Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID").getTextContent();
            String customerParty = lookup.getNode("//inv:Invoice/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID").getTextContent();
            String accountingParty;
            if (authTenant.getTenantInfo().getTc().equals(customerParty)) {
                invoice.setIsComingInvoice(true);
                accountingParty = "cac:AccountingSupplierParty";
            } else if (authTenant.getTenantInfo().getTc().equals(supplierParty)) {
                invoice.setIsComingInvoice(false);
                accountingParty = "cac:AccountingCustomerParty";
            } else {
                throw new CustomException("Bu faturayı sadece fatura tarafları görüntüleyebilir.");
            }
            invoice.setTenant(authTenant);
            customer.setTc(lookup.getNode("//inv:Invoice/" + accountingParty + "/cac:Party/cac:PartyIdentification/cbc:ID").getTextContent());
            if (customer.getTc().length() == 10) {
                customer.setAppellation(lookup.getNode("//inv:Invoice/" + accountingParty + "/cac:Party/cac:PartyName/cbc:Name").getTextContent());
                customer.setFullAppellation(lookup.getNode("//inv:Invoice/" + accountingParty + "/cac:Party/cac:PartyName/cbc:Name").getTextContent());
            } else {
                customer.setName(lookup.getNode("//inv:Invoice/" + accountingParty + "/cac:Party/cac:Person/cbc:FirstName").getTextContent());
                customer.setSurname(lookup.getNode("//inv:Invoice/" + accountingParty + "/cac:Party/cac:Person/cbc:FamilyName").getTextContent());
            }
            Address address = new Address();
            Node cityNode = lookup.getNode("//inv:Invoice/" + accountingParty + "/cac:Party/cac:PostalAddress/cbc:CityName");
            if (cityNode != null) {
                address.setCity(City.byCode(cityNode.getTextContent()));
                Node districtNode = lookup.getNode("//inv:Invoice/" + accountingParty + "/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName");
                if (address.getCity() != null && districtNode != null) {
                    address.setDistrict(District.byCode(address.getCity(), districtNode.getTextContent()));
                }
            }
            Node streetNode = lookup.getNode("//inv:Invoice/" + accountingParty + "/cac:Party/cac:PostalAddress/cbc:StreetName");
            if (streetNode != null) {
                address.setFullAddress(streetNode.getTextContent());
            }
            customer.setAddress(new ArrayList<Address>());
            customer.getAddress().add(address);
            customer.setTaxAdministration(
                    lookup.getNode("//inv:Invoice/" + accountingParty + "/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name").getTextContent());
            invoice.setCustomer(customer);

            invoice.setTaxesTotal(Double.parseDouble(
                    lookup.getNode("//inv:Invoice/cac:TaxTotal/cbc:TaxAmount").getTextContent()));

            NodeList taxSubTotal = lookup.getNodeList("//inv:Invoice/cac:TaxTotal/cac:TaxSubtotal");
            if (taxSubTotal.getLength() > 0) {
                List<InvoiceTaxes> invoiceTaxess = new ArrayList<>();
                for (int i = 0; i < taxSubTotal.getLength(); i++) {
                    InvoiceTaxes invoiceTaxes = new InvoiceTaxes();
                    String taxAmount = lookup.getNode("//inv:Invoice/cac:TaxTotal/cac:TaxSubtotal[" + (i + 1) + "]/cbc:TaxAmount").getTextContent();
                    invoiceTaxes.setAmount(Double.parseDouble(taxAmount));
                    Node percent = lookup.getNode("//inv:Invoice/cac:TaxTotal/cac:TaxSubtotal[" + (i + 1) + "]/cbc:Percent");
                    if (percent != null) {
                        invoiceTaxes.setValue(Long.parseLong(percent.getTextContent()));
                    }
                    String typeCode = lookup.getNode("//inv:Invoice/cac:TaxTotal/cac:TaxSubtotal[" + (i + 1) + "]/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode").getTextContent();
                    invoiceTaxes.setTaxesType(TaxesType.byCode(typeCode));
                    invoiceTaxess.add(invoiceTaxes);
                }
                invoice.setInvoiceTaxes(invoiceTaxess);
            }

            String lineExtension = lookup.getNode("//inv:Invoice/cac:LegalMonetaryTotal/cbc:LineExtensionAmount").getTextContent();
            invoice.setSubTotal(Double.parseDouble(lineExtension));
            String taxExclusive = lookup.getNode("//inv:Invoice/cac:LegalMonetaryTotal/cbc:TaxExclusiveAmount").getTextContent();
            invoice.setGrossTotal(Double.parseDouble(taxExclusive));
            //String taxInclusive=lookup.getNode("//inv:Invoice/cac:LegalMonetaryTotal/cbc:TaxInclusiveAmount").getTextContent();
            //invoice.setSubTotal(Double.parseDouble(taxInclusive));
            String payable = lookup.getNode("//inv:Invoice/cac:LegalMonetaryTotal/cbc:PayableAmount").getTextContent();
            invoice.setPriceTotal(Double.parseDouble(payable));
            Node allowanceTotal = lookup.getNode("//inv:Invoice/cac:LegalMonetaryTotal/cbc:AllowanceTotalAmount");
            if (allowanceTotal != null) {
                invoice.setDiscountTotal(Double.parseDouble(allowanceTotal.getTextContent()));
            }

            List<InvoiceLine> lines = new ArrayList<>();
            NodeList lineNode = lookup.getNodeList("//inv:Invoice/cac:InvoiceLine");
            for (int i = 0; i < lineNode.getLength(); i++) {
                InvoiceLine line = new InvoiceLine();
                Item item = new Item();
                Node invoicedQuantity = (Node) lookup.getNode("//inv:Invoice/cac:InvoiceLine[" + (i + 1) + "]/cbc:InvoicedQuantity");
                Element quantityElement = (Element) invoicedQuantity;
                item.setUomCode(UomCode.byCode(quantityElement.getAttribute("unitCode")));
                line.setQuantity(Double.parseDouble(invoicedQuantity.getTextContent()));
                String lineAmount = lookup.getNode("//inv:Invoice/cac:InvoiceLine[" + (i + 1) + "]/cbc:LineExtensionAmount").getTextContent();
                line.setGrossTotal(Double.parseDouble(lineAmount));
                item.setName(lookup.getNode("//inv:Invoice/cac:InvoiceLine[" + (i + 1) + "]/cac:Item/cbc:Name").getTextContent());
                String itemPrice = lookup.getNode("//inv:Invoice/cac:InvoiceLine[" + (i + 1) + "]/cac:Price/cbc:PriceAmount").getTextContent();
                item.setPrice(Double.parseDouble(itemPrice));

                line.setItem(item);
                lines.add(line);
            }
            invoice.setInvoiceLine(lines);

        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return invoice;
    }
}
