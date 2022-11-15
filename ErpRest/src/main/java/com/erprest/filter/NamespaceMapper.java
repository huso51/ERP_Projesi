/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.filter;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import tr.gov.efatura.xjc.DespatchAdviceType;

/**
 *
 * @author msi_ge72
 */
public class NamespaceMapper<T> extends NamespacePrefixMapper {

    private static final Logger logger = Logger.getLogger(NamespaceMapper.class.getName());
    HashMap<String, String> nsMap = new HashMap<>();

    public NamespaceMapper(T type) {
        if (type instanceof DespatchAdviceType) {
            nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:DespatchAdvice-2", "");
        }
        nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext");
        nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac");
        nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc");
        nsMap.put("http://www.altova.com/samplexml/other-namespace", "n4");
        nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:SignatureBasicComponents-2", "sbc");
        nsMap.put("http://www.w3.org/2000/09/xmldsig#", "ds");
        nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:SignatureAggregateComponents-2", "sac");
        nsMap.put("http://uri.etsi.org/01903/v1.3.2#", "xades");
        /*nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:CommonSignatureComponents-2", "");
        nsMap.put("http://uri.etsi.org/01903/v1.4.1#", "");
        nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "");
        nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "");
        nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2", "");
        nsMap.put("urn:oasis:names:specification:ubl:schema:xsd:ReceiptAdvice-2", "");*/
    }

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        String nsValue = nsMap.get(namespaceUri);
        if (nsValue == null) {
            //logger.log(Level.SEVERE, "{0} bulunamadi.", namespaceUri);
            return suggestion;
        } else {
            return nsValue;
        }
    }

}
