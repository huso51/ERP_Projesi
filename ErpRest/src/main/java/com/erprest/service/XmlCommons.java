/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import com.erprest.model.Customer;
import tr.gov.efatura.xjc.ObjectFactory;
import tr.gov.efatura.xjc.PartyType;

/**
 *
 * @author msi_ge72
 */
public class XmlCommons {

    public PartyType getPartyElement(PartyType partyType,Customer customer) {
        ObjectFactory factory = new ObjectFactory();
        String customerTc = customer.getTc();
        if (customerTc.length() == 10) {
            partyType.getPartyIdentification().get(0).getID().setSchemeID("VKN");
            partyType.setPartyName(factory.createPartyNameType());
            partyType.getPartyName().setName(factory.createNameType());
            partyType.getPartyName().getName().setValue(customer.getFullAppellation());
        } else {
            partyType.getPartyIdentification().get(0).getID().setSchemeID("TCKN");
            partyType.setPerson(factory.createPersonType());
            partyType.getPerson().setFirstName(factory.createFirstNameType());
            partyType.getPerson().getFirstName().setValue(customer.getName());
            partyType.getPerson().setFamilyName(factory.createFamilyNameType());
            partyType.getPerson().getFamilyName().setValue(customer.getSurname());
        }

        partyType.getPartyIdentification().get(0).getID().setValue(
                customerTc);
        partyType.getPostalAddress().getStreetName().setValue(
                customer.getAddress().get(0).getFullAddress());
        partyType.getPostalAddress().getCitySubdivisionName().setValue(
                customer.getAddress().get(0).getDistrict().getName());
        partyType.getPostalAddress().getCityName().setValue(
                customer.getAddress().get(0).getCity().getName());
        if (customer.getAddress().get(0).getNeighborhood().getPostCode() != 0) {
            partyType.getPostalAddress().setPostalZone(factory.createPostalZoneType());
            partyType.getPostalAddress().getPostalZone().setValue(
                    "" + customer.getAddress().get(0).getNeighborhood().getPostCode());
        }
        return partyType;
    }
}
