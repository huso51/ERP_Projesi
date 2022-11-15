/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import com.erprest.model.Invoice;
import com.erprest.model.InvoiceLine;
import com.erprest.model.InvoiceLineTaxes;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Base64;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 *
 * @author msi_ge72
 */
public class InvoiceXml {

    public String getInvoiceXml(Invoice invoice) {
        if (invoice.getInvoiceScenario().getCode().equals("TEMELIRSALIYE")) {
            return new DespatchInvoice().getXml(invoice);
        }
        String xmlString = null;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            document.setXmlStandalone(true);

            Element invoiceElement = document.createElement("Invoice");
            document.appendChild(invoiceElement);

            invoiceElement.setAttribute("xmlns", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
            invoiceElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            invoiceElement.setAttribute("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
            invoiceElement.setAttribute("xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
            invoiceElement.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
            invoiceElement.setAttribute("xmlns:xades", "http://uri.etsi.org/01903/v1.3.2#");
            invoiceElement.setAttribute("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
            invoiceElement.setAttribute("xsi:schemaLocation", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2 ..\\xsdrt\\maindoc\\UBL-Invoice-2.1.xsd");
            invoiceElement.setAttribute("xmlns:n4", "http://www.altova.com/samplexml/other-namespace");

            Element ublExtensions = document.createElement("ext:UBLExtensions");
            invoiceElement.appendChild(ublExtensions);

            Element ublExtension = document.createElement("ext:UBLExtension");
            ublExtensions.appendChild(ublExtension);

            Element extensionContent = document.createElement("ext:ExtensionContent");
            extensionContent.appendChild(document.createTextNode(" "));
            ublExtension.appendChild(extensionContent);

            Element ublVersion = document.createElement("cbc:UBLVersionID");
            Text ublVersionText = document.createTextNode("2.1");
            ublVersion.appendChild(ublVersionText);
            invoiceElement.appendChild(ublVersion);

            Element customizationId = document.createElement("cbc:CustomizationID");
            Text customizationIdText = document.createTextNode("TR1.2");
            customizationId.appendChild(customizationIdText);
            invoiceElement.appendChild(customizationId);

            Element profileId = document.createElement("cbc:ProfileID");
            String scenarioCode = "TEMELFATURA";
            if (invoice.getInvoiceScenario().getId() != 0) {
                scenarioCode = invoice.getInvoiceScenario().getCode();
            }
            Text profileIdText = document.createTextNode(scenarioCode);
            profileId.appendChild(profileIdText);
            invoiceElement.appendChild(profileId);

            Element id = document.createElement("cbc:ID");
            Text idText = document.createTextNode(invoice.getSn1() + invoice.getSn2() + invoice.getSn3());
            id.appendChild(idText);
            invoiceElement.appendChild(id);

            Element copyIndicator = document.createElement("cbc:CopyIndicator");
            Text copyIndicatorText = document.createTextNode("false");
            copyIndicator.appendChild(copyIndicatorText);
            invoiceElement.appendChild(copyIndicator);

            Element uuid = document.createElement("cbc:UUID");
            Text uuidText = document.createTextNode(invoice.getUuid());
            uuid.appendChild(uuidText);
            invoiceElement.appendChild(uuid);

            Element issueDate = document.createElement("cbc:IssueDate");
            Text issueDateText = document.createTextNode(invoice.getCreatedAt().split("\\s+")[0]);
            issueDate.appendChild(issueDateText);
            invoiceElement.appendChild(issueDate);

            Element issueTime = document.createElement("cbc:IssueTime");
            Text issueTimeText = document.createTextNode(invoice.getCreatedAt().split("\\s+")[1]);
            issueTime.appendChild(issueTimeText);
            invoiceElement.appendChild(issueTime);

            Element invoiceTypeCode = document.createElement("cbc:InvoiceTypeCode");
            Text invoiceTypeCodeText = document.createTextNode(invoice.getInvoiceType().getCode());
            invoiceTypeCode.appendChild(invoiceTypeCodeText);
            invoiceElement.appendChild(invoiceTypeCode);

            if (invoice.getNote() != null && !invoice.getNote().equals("")) {
                String noteLine[] = invoice.getNote().split("\\r?\\n");
                for (String note : noteLine) {
                    Element invoiceNote = document.createElement("cbc:Note");
                    invoiceNote.appendChild(document.createTextNode(note));
                    invoiceElement.appendChild(invoiceNote);
                }
            }

            Element documentCurrencyCode = document.createElement("cbc:DocumentCurrencyCode");
            Text documentCurrencyCodeText = document.createTextNode(invoice.getCurrency().getCode());
            documentCurrencyCode.appendChild(documentCurrencyCodeText);
            invoiceElement.appendChild(documentCurrencyCode);

            Element lineCountNumeric = document.createElement("cbc:LineCountNumeric");
            Text lineCountNumericText = document.createTextNode("" + invoice.getInvoiceLine().size());
            lineCountNumeric.appendChild(lineCountNumericText);
            invoiceElement.appendChild(lineCountNumeric);
            if (invoice.getOrderNo() != null && !invoice.getOrderNo().equals("") && invoice.getOrderDate() != null) {
                Element orderReferance = document.createElement("cac:OrderReference");
                invoiceElement.appendChild(orderReferance);
                Element orderId = document.createElement("cbc:ID");
                Text orderIdText = document.createTextNode(invoice.getOrderNo());
                orderId.appendChild(orderIdText);
                orderReferance.appendChild(orderId);

                Element orderIssueDate = document.createElement("cbc:IssueDate");
                Text orderIssueDateText = document.createTextNode(invoice.getOrderDate().split("\\s+")[0]);
                orderIssueDate.appendChild(orderIssueDateText);
                orderReferance.appendChild(orderIssueDate);
            }
            if (invoice.getWaybillNumber() != null && !invoice.getWaybillNumber().equals("") && invoice.getWaybillDate() != null) {
                Element despatchDocumentReference = document.createElement("cac:DespatchDocumentReference");
                invoiceElement.appendChild(despatchDocumentReference);

                Element despetchId = document.createElement("cbc:ID");
                Text despetchIdText = document.createTextNode(invoice.getWaybillNumber());
                despetchId.appendChild(despetchIdText);
                despatchDocumentReference.appendChild(despetchId);

                Element despetchIssueDate = document.createElement("cbc:IssueDate");
                Text despetchIssueDateText = document.createTextNode(invoice.getWaybillDate().split("\\s+")[0]);
                despetchIssueDate.appendChild(despetchIssueDateText);
                despatchDocumentReference.appendChild(despetchIssueDate);
            }
            Element AdditionalDocument = document.createElement("cac:AdditionalDocumentReference");
            invoiceElement.appendChild(AdditionalDocument);

            Element additionalId = document.createElement("cbc:ID");
            Text additionalIdText = document.createTextNode(invoice.getUuid());
            additionalId.appendChild(additionalIdText);
            AdditionalDocument.appendChild(additionalId);

            Element additionalIssueDate = document.createElement("cbc:IssueDate");
            Text additionalIssueDateText = document.createTextNode(invoice.getCreatedAt().split("\\s+")[0]);
            additionalIssueDate.appendChild(additionalIssueDateText);
            AdditionalDocument.appendChild(additionalIssueDate);

            Element additionalattachmennt = document.createElement("cac:Attachment");
            AdditionalDocument.appendChild(additionalattachmennt);
            Element Embedded = document.createElement("cbc:EmbeddedDocumentBinaryObject");
            Embedded.setAttribute("mimeCode", "application/xml");
            Embedded.setAttribute("filename", invoice.getSn1() + invoice.getSn2() + invoice.getSn3() + ".xslt");
            Embedded.setAttribute("encodingCode", "Base64");
            Embedded.setAttribute("characterSetCode", "UTF-8");
            Text EmbeddedText = document.createTextNode(new String(Base64.getEncoder().encode(
                    new InvoiceXslt().getInvoiceXslt(
                            invoice.getInvoiceScenario().getCode(),
                            invoice.getTenant().getLogo(),
                            invoice.getTenant().getSignature())
                            .getBytes("UTF-8"))));
            Embedded.appendChild(EmbeddedText);
            additionalattachmennt.appendChild(Embedded);

            Element signature = document.createElement("cac:Signature");
            invoiceElement.appendChild(signature);

            Element signitureId = document.createElement("cbc:ID");
            Text signitureIdText = document.createTextNode(invoice.getTenant().getTenantInfo().getTc());
            signitureId.setAttribute("schemeID", "VKN_TCKN");
            signitureId.appendChild(signitureIdText);
            signature.appendChild(signitureId);

            Element signatoryParty = document.createElement("cac:SignatoryParty");
            signature.appendChild(signatoryParty);

            Element partyIdentification = document.createElement("cac:PartyIdentification");
            signatoryParty.appendChild(partyIdentification);

            String tcOrVkn = "TCKN";
            if (invoice.getTenant().getTenantInfo().getTc().length() == 10) {
                tcOrVkn = "VKN";
            }
            Element partyId = document.createElement("cbc:ID");
            partyId.setAttribute("schemeID", tcOrVkn);
            Text partyIdText = document.createTextNode(invoice.getTenant().getTenantInfo().getTc());
            partyId.appendChild(partyIdText);
            partyIdentification.appendChild(partyId);

            Element postalAddress = document.createElement("cac:PostalAddress");
            signatoryParty.appendChild(postalAddress);

            Element streetName = document.createElement("cbc:StreetName");
            Text streetNameText = document.createTextNode(invoice.getTenant().getTenantInfo().getAddress().get(0).getFullAddress());
            streetName.appendChild(streetNameText);
            postalAddress.appendChild(streetName);

            Element citySubdivision = document.createElement("cbc:CitySubdivisionName");
            Text citySubdivisionText = document.createTextNode(invoice.getTenant().getTenantInfo().getAddress().get(0).getDistrict().getName());
            citySubdivision.appendChild(citySubdivisionText);
            postalAddress.appendChild(citySubdivision);

            Element cityName = document.createElement("cbc:CityName");
            Text cityNameText = document.createTextNode(invoice.getTenant().getTenantInfo().getAddress().get(0).getCity().getName());
            cityName.appendChild(cityNameText);
            postalAddress.appendChild(cityName);

            if (invoice.getTenant().getTenantInfo().getAddress().get(0).getNeighborhood().getPostCode() != 0) {
                Element postalZone = document.createElement("cbc:PostalZone");
                Text postalZoneText = document.createTextNode("" + invoice.getTenant().getTenantInfo().getAddress().get(0).getNeighborhood().getPostCode());
                postalZone.appendChild(postalZoneText);
                postalAddress.appendChild(postalZone);
            }

            Element country = document.createElement("cac:Country");
            postalAddress.appendChild(country);

            Element countryName = document.createElement("cbc:Name");
            Text countryNameText = document.createTextNode("Türkiye");
            countryName.appendChild(countryNameText);
            country.appendChild(countryName);

            Element signatureAttachment = document.createElement("cac:DigitalSignatureAttachment");
            signature.appendChild(signatureAttachment);

            Element externalReference = document.createElement("cac:ExternalReference");
            signatureAttachment.appendChild(externalReference);

            Element externalUri = document.createElement("cbc:URI");
            Text externalUriText = document.createTextNode("s-" + invoice.getUuid());
            externalUri.appendChild(externalUriText);
            externalReference.appendChild(externalUri);

            Element accountingSupplier = document.createElement("cac:AccountingSupplierParty");
            invoiceElement.appendChild(accountingSupplier);

            Element party = document.createElement("cac:Party");
            accountingSupplier.appendChild(party);
            /*
            Element websiteURI = document.createElement("cbc:WebsiteURI");
            Text websiteURIText = document.createTextNode("http://www.aaa.com.tr/");
            websiteURI.appendChild(websiteURIText);
            party.appendChild(websiteURI);
             */
            Element partyIdent = document.createElement("cac:PartyIdentification");
            party.appendChild(partyIdent);

            Element identId = document.createElement("cbc:ID");
            Text identIdText = document.createTextNode(invoice.getTenant().getTenantInfo().getTc());
            identId.setAttribute("schemeID", tcOrVkn);
            identId.appendChild(identIdText);
            partyIdent.appendChild(identId);

            if (invoice.getTenant().getTenantInfo().getTc().length() == 10) {
                Element partyName = document.createElement("cac:PartyName");
                party.appendChild(partyName);

                Element partyNameName = document.createElement("cbc:Name");
                Text partyNameNameText = document.createTextNode(invoice.getTenant().getTenantInfo().getFullAppellation());
                partyNameName.appendChild(partyNameNameText);
                partyName.appendChild(partyNameName);
            } else if (invoice.getTenant().getTenantInfo().getTc().length() == 11) {
                Element tenantPerson = document.createElement("cac:Person");
                party.appendChild(tenantPerson);

                Element tenantPartyNameName = document.createElement("cbc:FirstName");
                Text tenantPartyNameNameText = document.createTextNode(invoice.getTenant().getTenantInfo().getName());
                tenantPartyNameName.appendChild(tenantPartyNameNameText);
                tenantPerson.appendChild(tenantPartyNameName);

                Element customerPartyNameName2 = document.createElement("cbc:FamilyName");
                Text customerPartyNameNameText2 = document.createTextNode(invoice.getTenant().getTenantInfo().getSurname());
                customerPartyNameName2.appendChild(customerPartyNameNameText2);
                tenantPerson.appendChild(customerPartyNameName2);
            }

            Element partyPostal = document.createElement("cac:PostalAddress");
            party.appendChild(partyPostal);

            Element partyPostalId = document.createElement("cbc:ID");
            Text partyPostalIdText = document.createTextNode(invoice.getTenant().getTenantInfo().getAddress().get(0).getAddressName());
            partyPostalId.appendChild(partyPostalIdText);
            partyPostal.appendChild(partyPostalId);

            /*if (invoice.getTenant().getTenantInfo().getAddress().get(0).getNeighborhood().getName() != null) {
                Element partyStreetName = document.createElement("cbc:StreetName");
                Text partyStreetNameText = document.createTextNode(invoice.getTenant().getTenantInfo().getAddress().get(0).getNeighborhood().getName());
                partyStreetName.appendChild(partyStreetNameText);
                partyPostal.appendChild(partyStreetName);
            }*/
            //html görüntülemede tam adres gözükmesi istendiğinden "cbc:StreetName" alanına tam adres olarak atandı.
            Element partyStreetName = document.createElement("cbc:StreetName");
            Text partyStreetNameText = document.createTextNode(invoice.getTenant().getTenantInfo().getAddress().get(0).getFullAddress());
            partyStreetName.appendChild(partyStreetNameText);
            partyPostal.appendChild(partyStreetName);

            Element partyCitySubdivision = document.createElement("cbc:CitySubdivisionName");
            Text partyCitySubdivisionText = document.createTextNode(invoice.getTenant().getTenantInfo().getAddress().get(0).getDistrict().getName());
            partyCitySubdivision.appendChild(partyCitySubdivisionText);
            partyPostal.appendChild(partyCitySubdivision);

            Element partyCityName = document.createElement("cbc:CityName");
            Text partyCityNameText = document.createTextNode(invoice.getTenant().getTenantInfo().getAddress().get(0).getCity().getName());
            partyCityName.appendChild(partyCityNameText);
            partyPostal.appendChild(partyCityName);

            if (invoice.getTenant().getTenantInfo().getAddress().get(0).getNeighborhood().getPostCode() != 0) {
                Element partyPostalZone = document.createElement("cbc:PostalZone");
                Text partyPostalZoneText = document.createTextNode("" + invoice.getTenant().getTenantInfo().getAddress().get(0).getNeighborhood().getPostCode());
                partyPostalZone.appendChild(partyPostalZoneText);
                partyPostal.appendChild(partyPostalZone);
            }

            Element partyCountry = document.createElement("cac:Country");
            partyPostal.appendChild(partyCountry);

            Element partyCountryName = document.createElement("cbc:Name");
            Text partyCountryNameText = document.createTextNode("Türkiye");
            partyCountryName.appendChild(partyCountryNameText);
            partyCountry.appendChild(partyCountryName);

            Element PartyTaxScheme = document.createElement("cac:PartyTaxScheme");
            party.appendChild(PartyTaxScheme);

            Element taxScheme = document.createElement("cac:TaxScheme");
            PartyTaxScheme.appendChild(taxScheme);

            Element taxName = document.createElement("cbc:Name");
            Text taxNameText = document.createTextNode(invoice.getTenant().getTenantInfo().getTaxAdministration());
            taxName.appendChild(taxNameText);
            taxScheme.appendChild(taxName);

            Element customerAccountingParty = document.createElement("cac:AccountingCustomerParty");
            invoiceElement.appendChild(customerAccountingParty);

            Element customerParty = document.createElement("cac:Party");
            customerAccountingParty.appendChild(customerParty);

            Element customerIdentifications = document.createElement("cac:PartyIdentification");
            customerParty.appendChild(customerIdentifications);

            String tcOrVkn2 = "TCKN";
            if (invoice.getCustomer().getTc().length() == 10) {
                tcOrVkn2 = "VKN";
                Element customerPartyName = document.createElement("cac:PartyName");
                customerParty.appendChild(customerPartyName);

                Element customerPartyNameName = document.createElement("cbc:Name");
                Text customerPartyNameNameText = document.createTextNode(invoice.getCustomer().getFullAppellation());
                customerPartyNameName.appendChild(customerPartyNameNameText);
                customerPartyName.appendChild(customerPartyNameName);
            }

            Element customerId = document.createElement("cbc:ID");
            Text customerIdText = document.createTextNode(invoice.getCustomer().getTc());
            customerId.setAttribute("schemeID", tcOrVkn2);
            customerId.appendChild(customerIdText);
            customerIdentifications.appendChild(customerId);

            Element customerpartyPostal = document.createElement("cac:PostalAddress");
            customerParty.appendChild(customerpartyPostal);

            Element customerpartyPostalId = document.createElement("cbc:ID");
            Text customerpartyPostalIdText = document.createTextNode(invoice.getCustomer().getAddress().get(0).getAddressName());
            customerpartyPostalId.appendChild(customerpartyPostalIdText);
            customerpartyPostal.appendChild(customerpartyPostalId);

            /*if (invoice.getCustomer().getAddress().get(0).getNeighborhood().getName() != null) {
                Element customerpartyStreetName = document.createElement("cbc:StreetName");
                Text customerpartyStreetNameText = document.createTextNode(invoice.getCustomer().getAddress().get(0).getNeighborhood().getName());
                customerpartyStreetName.appendChild(customerpartyStreetNameText);
                customerpartyPostal.appendChild(customerpartyStreetName);
            }*/
            //html görüntülemede tam adres gözükmesi istendiğinden "cbc:StreetName" alanına tam adres olarak atandı.
            Element customerpartyStreetName = document.createElement("cbc:StreetName");
            Text customerpartyStreetNameText = document.createTextNode(invoice.getCustomer().getAddress().get(0).getFullAddress());
            customerpartyStreetName.appendChild(customerpartyStreetNameText);
            customerpartyPostal.appendChild(customerpartyStreetName);

            Element customerpartyCitySubdivision = document.createElement("cbc:CitySubdivisionName");
            Text customerpartyCitySubdivisionText = document.createTextNode(invoice.getCustomer().getAddress().get(0).getDistrict().getName());
            customerpartyCitySubdivision.appendChild(customerpartyCitySubdivisionText);
            customerpartyPostal.appendChild(customerpartyCitySubdivision);

            Element customerpartyCityName = document.createElement("cbc:CityName");
            Text customerpartyCityNameText = document.createTextNode(invoice.getCustomer().getAddress().get(0).getCity().getName());
            customerpartyCityName.appendChild(customerpartyCityNameText);
            customerpartyPostal.appendChild(customerpartyCityName);

            if (invoice.getCustomer().getAddress().get(0).getNeighborhood().getPostCode() != 0) {
                Element customerpartyPostalZone = document.createElement("cbc:PostalZone");
                Text customerpartyPostalZoneText = document.createTextNode("" + invoice.getCustomer().getAddress().get(0).getNeighborhood().getPostCode());
                customerpartyPostalZone.appendChild(customerpartyPostalZoneText);
                customerpartyPostal.appendChild(customerpartyPostalZone);
            }

            Element customerpartyCountry = document.createElement("cac:Country");
            customerpartyPostal.appendChild(customerpartyCountry);

            Element customerpartyCountryName = document.createElement("cbc:Name");
            Text customerpartyCountryNameText = document.createTextNode("Türkiye");
            customerpartyCountryName.appendChild(customerpartyCountryNameText);
            customerpartyCountry.appendChild(customerpartyCountryName);

            Element customerPartyTaxScheme = document.createElement("cac:PartyTaxScheme");
            customerParty.appendChild(customerPartyTaxScheme);

            Element customertaxScheme = document.createElement("cac:TaxScheme");
            customerPartyTaxScheme.appendChild(customertaxScheme);

            Element customertaxName = document.createElement("cbc:Name");
            Text customertaxNameText = document.createTextNode(invoice.getCustomer().getTaxAdministration());
            customertaxName.appendChild(customertaxNameText);
            customertaxScheme.appendChild(customertaxName);

            if (invoice.getCustomer().getTc().length() == 11) {
                tcOrVkn2 = "TCKN";
                Element customerPerson = document.createElement("cac:Person");
                customerParty.appendChild(customerPerson);

                Element customerPartyNameName = document.createElement("cbc:FirstName");
                Text customerPartyNameNameText = document.createTextNode(invoice.getCustomer().getName());
                customerPartyNameName.appendChild(customerPartyNameNameText);
                customerPerson.appendChild(customerPartyNameName);

                Element customerPartyNameName2 = document.createElement("cbc:FamilyName");
                Text customerPartyNameNameText2 = document.createTextNode(invoice.getCustomer().getSurname());
                customerPartyNameName2.appendChild(customerPartyNameNameText2);
                customerPerson.appendChild(customerPartyNameName2);
            }

            Element taxTotal = document.createElement("cac:TaxTotal");
            invoiceElement.appendChild(taxTotal);

            Element taxAmount = document.createElement("cbc:TaxAmount");
            Text taxAmountText = document.createTextNode("" + invoice.getTaxesTotal());
            taxAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
            taxAmount.appendChild(taxAmountText);
            taxTotal.appendChild(taxAmount);
            if (invoice.getInvoiceTaxes() != null) {
                for (int i = 0; i < invoice.getInvoiceTaxes().size(); i++) {
                    Element taxSubtotal = document.createElement("cac:TaxSubtotal");
                    taxTotal.appendChild(taxSubtotal);

                    Element taxableAmount = document.createElement("cbc:TaxableAmount");
                    Text taxableAmountText = document.createTextNode("" + invoice.getGrossTotal());
                    taxableAmount.appendChild(taxableAmountText);
                    taxableAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
                    taxSubtotal.appendChild(taxableAmount);

                    Element taxAmount2 = document.createElement("cbc:TaxAmount");
                    Text taxAmount2Text = document.createTextNode("" + invoice.getInvoiceTaxes().get(i).getAmount());
                    taxAmount2.appendChild(taxAmount2Text);
                    taxAmount2.setAttribute("currencyID", invoice.getCurrency().getCode());
                    taxSubtotal.appendChild(taxAmount2);

                    Element taxpercent = document.createElement("cbc:Percent");
                    Text taxpercentText = document.createTextNode("" + invoice.getInvoiceTaxes().get(i).getValue());
                    taxpercent.appendChild(taxpercentText);
                    taxSubtotal.appendChild(taxpercent);

                    Element taxCategory = document.createElement("cac:TaxCategory");
                    taxSubtotal.appendChild(taxCategory);

                    if (invoice.getInvoiceException() != null && invoice.getInvoiceException().getId() != 0) {
                        Element exemptionReasonCode = document.createElement("cbc:TaxExemptionReasonCode");
                        Text exemptionReasonCodetext = document.createTextNode(invoice.getInvoiceException().getCode());
                        exemptionReasonCode.appendChild(exemptionReasonCodetext);
                        taxCategory.appendChild(exemptionReasonCode);

                        Element exemptionReason = document.createElement("cbc:TaxExemptionReason");
                        Text exemptionReasontext = document.createTextNode(invoice.getInvoiceException().getDescription());
                        exemptionReason.appendChild(exemptionReasontext);
                        taxCategory.appendChild(exemptionReason);
                    }
                    Element taxtaxScheme = document.createElement("cac:TaxScheme");
                    taxCategory.appendChild(taxtaxScheme);

                    Element taxtaxName = document.createElement("cbc:Name");
                    Text taxtaxNameText = document.createTextNode(invoice.getInvoiceTaxes().get(i).getTaxesType().getDescription());
                    taxtaxName.appendChild(taxtaxNameText);
                    taxtaxScheme.appendChild(taxtaxName);

                    Element taxtaxCode = document.createElement("cbc:TaxTypeCode");
                    //Text taxtaxCodeText = document.createTextNode(invoice.getInvoiceTaxes().get(i).getTaxesType().getCode());
                    Text taxtaxCodeText = document.createTextNode(invoice.getInvoiceTaxes().get(i).getTaxesType().getCode());

                    taxtaxCode.appendChild(taxtaxCodeText);
                    taxtaxScheme.appendChild(taxtaxCode);

                }
            }
            /////////////////////// eger varsa
            if (invoice.getInvoiceWithholdingId() != 0) {
                double withholdingAmount = 0;
                for (int j = 0; j < invoice.getInvoiceTaxes().size(); j++) {
                    if (invoice.getInvoiceTaxes().get(j).getTaxesType().getCode().toLowerCase().contains("kdv")) {
                        withholdingAmount = (invoice.getInvoiceTaxes().get(j).getAmount() * (double) invoice.getInvoiceWithholding().getValue()) / 100;
                    }
                }
                Element WithholdingTaxTotal = document.createElement("cac:WithholdingTaxTotal");
                invoiceElement.appendChild(WithholdingTaxTotal);

                Element withholdingTaxAmount = document.createElement("cbc:TaxAmount");
                Text withholdingTaxAmountText = document.createTextNode("" + withholdingAmount);
                withholdingTaxAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
                withholdingTaxAmount.appendChild(withholdingTaxAmountText);
                WithholdingTaxTotal.appendChild(withholdingTaxAmount);

                Element withholdingTaxSubtotal = document.createElement("cac:TaxSubtotal");
                WithholdingTaxTotal.appendChild(withholdingTaxSubtotal);

                Element withholdingtaxAmount2 = document.createElement("cbc:TaxAmount");

                Text withholdingtaxAmount2Text = document.createTextNode("" + withholdingAmount);
                withholdingtaxAmount2.appendChild(withholdingtaxAmount2Text);
                withholdingtaxAmount2.setAttribute("currencyID", invoice.getCurrency().getCode());
                withholdingTaxSubtotal.appendChild(withholdingtaxAmount2);

                Element withholdingtaxpercent = document.createElement("cbc:Percent");
                Text withholdingtaxpercentText = document.createTextNode("" + invoice.getInvoiceWithholding().getValue());
                withholdingtaxpercent.appendChild(withholdingtaxpercentText);
                withholdingTaxSubtotal.appendChild(withholdingtaxpercent);

                Element withholdingtaxCategory = document.createElement("cac:TaxCategory");
                withholdingTaxSubtotal.appendChild(withholdingtaxCategory);

                Element withholdingtaxtaxScheme = document.createElement("cac:TaxScheme");
                withholdingtaxCategory.appendChild(withholdingtaxtaxScheme);

                Element withholdingtaxtaxCode = document.createElement("cbc:TaxTypeCode");
                Text withholdingtaxtaxCodeText = document.createTextNode(invoice.getInvoiceWithholding().getCode());
                withholdingtaxtaxCode.appendChild(withholdingtaxtaxCodeText);
                withholdingtaxtaxScheme.appendChild(withholdingtaxtaxCode);
            }
            ////////////////////////
            Element monetaryTotal = document.createElement("cac:LegalMonetaryTotal");
            invoiceElement.appendChild(monetaryTotal);

            Element lineExtensionTotal = document.createElement("cbc:LineExtensionAmount");
            Text lineExtensionTotalText = document.createTextNode("" + invoice.getSubTotal());
            lineExtensionTotal.setAttribute("currencyID", invoice.getCurrency().getCode());
            lineExtensionTotal.appendChild(lineExtensionTotalText);
            monetaryTotal.appendChild(lineExtensionTotal);

            Element exclusiveAmount = document.createElement("cbc:TaxExclusiveAmount");
            Text exclusiveAmountText = document.createTextNode("" + invoice.getGrossTotal());
            exclusiveAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
            exclusiveAmount.appendChild(exclusiveAmountText);
            monetaryTotal.appendChild(exclusiveAmount);

            BigDecimal taxInclusive = new BigDecimal("" + invoice.getGrossTotal());
            taxInclusive = taxInclusive.add(new BigDecimal("" + invoice.getTaxesTotal()));
            Element inclusiveAmount = document.createElement("cbc:TaxInclusiveAmount");
            Text inclusiveAmountText = document.createTextNode("" + taxInclusive);
            inclusiveAmount.appendChild(inclusiveAmountText);
            inclusiveAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
            monetaryTotal.appendChild(inclusiveAmount);

            Element allowanceTotalAmount = document.createElement("cbc:AllowanceTotalAmount");
            Text allowanceTotalAmountText = document.createTextNode("" + invoice.getDiscountTotal());
            allowanceTotalAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
            allowanceTotalAmount.appendChild(allowanceTotalAmountText);
            monetaryTotal.appendChild(allowanceTotalAmount);

            Element payableAmount = document.createElement("cbc:PayableAmount");
            Text payableAmountText = document.createTextNode("" + invoice.getPriceTotal());
            payableAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
            payableAmount.appendChild(payableAmountText);
            monetaryTotal.appendChild(payableAmount);

            //1.for
            for (int i = 0; i < invoice.getInvoiceLine().size(); i++) {
                InvoiceLine line = invoice.getInvoiceLine().get(i);
                Element invoiceLine = document.createElement("cac:InvoiceLine");
                invoiceElement.appendChild(invoiceLine);

                Element invoiceLineId = document.createElement("cbc:ID");
                Text middlenameText73 = document.createTextNode("" + (i + 1));
                invoiceLineId.appendChild(middlenameText73);
                invoiceLine.appendChild(invoiceLineId);

                Element invoiceLineQuantity = document.createElement("cbc:InvoicedQuantity");
                Text invoiceLineQuantityText = document.createTextNode("" + line.getQuantity());
                invoiceLineQuantity.appendChild(invoiceLineQuantityText);
                invoiceLineQuantity.setAttribute("unitCode", line.getItem().getUomCode().getCode());
                invoiceLine.appendChild(invoiceLineQuantity);

                Element lineExtensionAmount = document.createElement("cbc:LineExtensionAmount");
                Text lineExtensionAmountText = document.createTextNode("" + line.getGrossTotal());
                lineExtensionAmount.appendChild(lineExtensionAmountText);
                lineExtensionAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
                invoiceLine.appendChild(lineExtensionAmount);

                Element allowanceCharge = document.createElement("cac:AllowanceCharge");
                invoiceLine.appendChild(allowanceCharge);

                Element chargeIndicator = document.createElement("cbc:ChargeIndicator");
                Text chargeIndicatorText = document.createTextNode("false");
                chargeIndicator.appendChild(chargeIndicatorText);
                allowanceCharge.appendChild(chargeIndicator);

                Element factorNumeric = document.createElement("cbc:MultiplierFactorNumeric");
                Text factorNumericText = document.createTextNode("" + ((double) line.getDiscountAmount() / 100));
                factorNumeric.appendChild(factorNumericText);
                allowanceCharge.appendChild(factorNumeric);

                Element lineAmount = document.createElement("cbc:Amount");
                Text lineAmountText = document.createTextNode("" + line.getDiscountTotal());
                lineAmount.appendChild(lineAmountText);
                lineAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
                allowanceCharge.appendChild(lineAmount);

                Element baseAmount = document.createElement("cbc:BaseAmount");
                Text baseAmountText = document.createTextNode("" + line.getSubTotal());
                baseAmount.appendChild(baseAmountText);
                baseAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
                allowanceCharge.appendChild(baseAmount);

                Element lineTaxTotal = document.createElement("cac:TaxTotal");
                invoiceLine.appendChild(lineTaxTotal);

                Element lineTaxAmount = document.createElement("cbc:TaxAmount");
                Text lineTaxAmountText = document.createTextNode("" + line.getTaxesTotal());
                lineTaxAmount.appendChild(lineTaxAmountText);
                lineTaxAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
                lineTaxTotal.appendChild(lineTaxAmount);

                for (int j = 0; j < line.getInvoiceLineTaxes().size(); j++) {
                    InvoiceLineTaxes lineTaxes = line.getInvoiceLineTaxes().get(j);
                    Element lineTaxSubTotal = document.createElement("cac:TaxSubtotal");
                    lineTaxTotal.appendChild(lineTaxSubTotal);

                    Element lineTaxableAmount = document.createElement("cbc:TaxableAmount");
                    Text lineTaxableAmountText = document.createTextNode("" + line.getGrossTotal());
                    lineTaxableAmount.appendChild(lineTaxableAmountText);
                    lineTaxableAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
                    lineTaxSubTotal.appendChild(lineTaxableAmount);

                    Element lineTaxAmount2 = document.createElement("cbc:TaxAmount");
                    Text lineTaxAmount2Text = document.createTextNode("" + lineTaxes.getAmount());
                    lineTaxAmount2.appendChild(lineTaxAmount2Text);
                    lineTaxAmount2.setAttribute("currencyID", invoice.getCurrency().getCode());
                    lineTaxSubTotal.appendChild(lineTaxAmount2);

                    Element linePercent = document.createElement("cbc:Percent");
                    Text linePercentText = document.createTextNode("" + lineTaxes.getValue());
                    linePercent.appendChild(linePercentText);
                    lineTaxSubTotal.appendChild(linePercent);

                    Element lineTaxCategory = document.createElement("cac:TaxCategory");
                    lineTaxSubTotal.appendChild(lineTaxCategory);
                    if (invoice.getInvoiceException() != null && invoice.getInvoiceException().getId() != 0) {
                        Element exemptionReasonCode2 = document.createElement("cbc:TaxExemptionReasonCode");
                        Text exemptionReasonCodetext2 = document.createTextNode(invoice.getInvoiceException().getCode());
                        exemptionReasonCode2.appendChild(exemptionReasonCodetext2);
                        lineTaxCategory.appendChild(exemptionReasonCode2);

                        Element exemptionReason2 = document.createElement("cbc:TaxExemptionReason");
                        Text exemptionReasontext2 = document.createTextNode(invoice.getInvoiceException().getDescription());
                        exemptionReason2.appendChild(exemptionReasontext2);
                        lineTaxCategory.appendChild(exemptionReason2);
                    }
                    Element lineTaxScheme = document.createElement("cac:TaxScheme");
                    lineTaxCategory.appendChild(lineTaxScheme);

                    Element lineName = document.createElement("cbc:Name");
                    Text lineNameText = document.createTextNode(lineTaxes.getTaxesType().getDescription());
                    lineName.appendChild(lineNameText);
                    lineTaxScheme.appendChild(lineName);

                    Element lineTaxTypeCode = document.createElement("cbc:TaxTypeCode");
                    //Text lineTaxTypeCodeText = document.createTextNode(lineTaxes.getTaxesType().getCode());
                    Text lineTaxTypeCodeText = document.createTextNode("" + lineTaxes.getTaxesType().getCode());
                    lineTaxTypeCode.appendChild(lineTaxTypeCodeText);
                    lineTaxScheme.appendChild(lineTaxTypeCode);
                    //
                }
                Element lineItem = document.createElement("cac:Item");
                invoiceLine.appendChild(lineItem);

                Element lineItemName = document.createElement("cbc:Name");
                Text lineItemNameText = document.createTextNode(line.getItem().getName());
                lineItemName.appendChild(lineItemNameText);
                lineItem.appendChild(lineItemName);

                Element lineItemIdent = document.createElement("cac:SellersItemIdentification");
                lineItem.appendChild(lineItemIdent);
                String itemIdentification = line.getItem().getDescription();
                if (line.getItem().getDescription() == null || itemIdentification.equals("")) {
                    itemIdentification = " ";
                }
                Element itemIdentId = document.createElement("cbc:ID");
                Text itemIdentIdText = document.createTextNode(itemIdentification);
                itemIdentId.appendChild(itemIdentIdText);
                lineItemIdent.appendChild(itemIdentId);

                Element linePrice = document.createElement("cac:Price");
                invoiceLine.appendChild(linePrice);

                Element linePriceAmount = document.createElement("cbc:PriceAmount");
                Text linePriceAmountText = document.createTextNode("" + line.getItem().getPrice());
                linePriceAmount.appendChild(linePriceAmountText);
                linePriceAmount.setAttribute("currencyID", invoice.getCurrency().getCode());
                linePrice.appendChild(linePriceAmount);
            }

            DOMSource xmlSource = new DOMSource(document);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Result outputTarget = new StreamResult(outputStream);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlSource, outputTarget);
            //isXml = new ByteArrayInputStream(outputStream.toString("UTF-8").getBytes(StandardCharsets.UTF_8));
            xmlString = (outputStream.toString("UTF-8"));

        } catch (UnsupportedEncodingException | TransformerException | ParserConfigurationException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return xmlString;
    }

    public static String domToString(Document doc) {//this for test
        StringWriter sw = null;
        try {
            sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));

        } catch (IllegalArgumentException | TransformerException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return sw.toString();
    }
}
