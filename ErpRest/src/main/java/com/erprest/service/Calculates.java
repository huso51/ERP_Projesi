/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import com.erprest.model.Invoice;
import com.erprest.model.InvoiceTaxes;
import com.erprest.model.ItemTaxes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author msi_ge72
 */
public class Calculates {

    public Invoice CalculateInvoice(Invoice invoice) {
        double taxesTotal = 0, discountTotal = 0, lineTotal = 0, subTotal = 0, grossTotal = 0, otvTotal = 0, priceTotal;

        for (int i = 0; i < invoice.getInvoiceLine().size(); i++) {
            double withMultiplier = invoice.getInvoiceLine().get(i).getItem().getPrice() * invoice.getInvoiceLine().get(i).getCurrencyMultiplier();
            invoice.getInvoiceLine().get(i).getItem().setPrice(round(withMultiplier, 2));
            double discount = 0, taxes = 0, linePrice = 0, otv = 0, tax;

            double lineSub = invoice.getInvoiceLine().get(i).getItem().getPrice() * invoice.getInvoiceLine().get(i).getQuantity();
            discount = discount + (((double) invoice.getInvoiceLine().get(i).getItem().getPrice() * (double) invoice.getInvoiceLine().get(i).getDiscountAmount()) / 100) * invoice.getInvoiceLine().get(i).getQuantity();
            double gross = lineSub * ((100 - (double) invoice.getInvoiceLine().get(i).getDiscountAmount()) / 100);
            linePrice = gross;
            List<ItemTaxes> itemTaxes = invoice.getInvoiceLine().get(i).getItem().getItemTaxes();

            for (int j = 0; j < itemTaxes.size(); j++) {
                if (itemTaxes.get(j).getTaxesType().getDescription().toLowerCase().equals("ötv")) {
                    otv = linePrice * (((double) itemTaxes.get(j).getValue()) / 100);
                    taxes = taxes + otv;
                    itemTaxes.get(j).getTaxesType().setTaxAmount(round(otv, 2));
                    linePrice = linePrice + linePrice * (((double) itemTaxes.get(j).getValue()) / 100);
                }
            }

            invoice.getInvoiceLine().get(i).setOtvIncludedTotal(round(linePrice, 2));
            double lineötv = linePrice;
            for (int j = 0; j < itemTaxes.size(); j++) {
                if (!itemTaxes.get(j).getTaxesType().getDescription().toLowerCase().equals("ötv")) {
                    tax = lineötv * (((double) itemTaxes.get(j).getValue()) / 100);
                    if (invoice.getInvoiceType().getCode().equals("TEVKIFAT") && itemTaxes.get(j).getTaxesType().getDescription().toLowerCase().contains("kdv")) {
                        tax = tax * ((100 - (double) invoice.getInvoiceWithholding().getValue()) / 100);
                    }
                    if (invoice.getInvoiceException() != null && itemTaxes.get(j).getTaxesType().getDescription().toLowerCase().equals("kdv")) {
                        tax = 0;
                    }
                    taxes = taxes + tax;
                    itemTaxes.get(j).getTaxesType().setTaxAmount(round(tax, 2));
                    linePrice = linePrice + tax;
                }
            }

            taxesTotal = taxesTotal + taxes;
            discountTotal = discountTotal + discount;
            lineTotal = lineTotal + linePrice;
            subTotal = subTotal + lineSub;
            grossTotal = grossTotal + gross;
            otvTotal = otvTotal + otv;
            priceTotal = invoice.getInvoiceLine().get(i).getItem().getPrice();
            //calc.round(10.1234567989, 2)
            invoice.getInvoiceLine().get(i).setPrice(round(priceTotal, 2));
            invoice.getInvoiceLine().get(i).setTaxesTotal(round(taxes, 2));
            invoice.getInvoiceLine().get(i).setLastPrice(round(linePrice, 2));
            invoice.getInvoiceLine().get(i).setDiscountTotal(round(discount, 2));
            invoice.getInvoiceLine().get(i).setSubTotal(round(lineSub, 2));
            invoice.getInvoiceLine().get(i).setGrossTotal(round(gross, 2));

        }
        invoice.setTaxesTotal(round(taxesTotal, 2));
        invoice.setPriceTotal(round(lineTotal, 2));
        invoice.setDiscountTotal(round(discountTotal, 2));
        invoice.setSubTotal(round(subTotal, 2));
        invoice.setGrossTotal(round(grossTotal, 2));
        invoice.setOtvTotal(round(otvTotal, 2));

        for (int i = 0; i < invoice.getInvoiceLine().size(); i++) {
            List<ItemTaxes> itemTaxes = invoice.getInvoiceLine().get(i).getItem().getItemTaxes();
            for (int j = 0; j < itemTaxes.size(); j++) {

                boolean kontrol = false;
                if (invoice.getInvoiceTaxes() != null) {
                    for (int k = 0; k < invoice.getInvoiceTaxes().size(); k++) {
                        if (invoice.getInvoiceTaxes() != null
                                && invoice.getInvoiceTaxes().get(k).getTaxesType().getCode().equals(itemTaxes.get(j).getTaxesType().getCode())
                                && invoice.getInvoiceTaxes().get(k).getValue() == itemTaxes.get(j).getValue()) {
                            kontrol = true;
                        }
                    }
                }

                if (!kontrol) {
                    List<InvoiceTaxes> invoiceTaxess = new ArrayList<>();
                    InvoiceTaxes invoiceTaxes = new InvoiceTaxes();
                    if (invoice.getInvoiceTaxes() == null) {
                        invoiceTaxes.setId(0 + 1);
                        invoice.setInvoiceTaxes(invoiceTaxess);
                    } else {
                        invoiceTaxes.setId(invoice.getInvoiceTaxes().size() + 1);
                    }
                    invoiceTaxes.setInvoiceId(invoice.getId());
                    invoiceTaxes.setTaxesType(itemTaxes.get(j).getTaxesType());
                    invoiceTaxes.setAmount(0);
                    invoiceTaxes.setValue(itemTaxes.get(j).getValue());
                    invoice.getInvoiceTaxes().add(invoiceTaxes);
                }
                for (int k = 0; k < invoice.getInvoiceTaxes().size(); k++) {
                    if (invoice.getInvoiceTaxes().get(k).getTaxesType().getCode().equals(itemTaxes.get(j).getTaxesType().getCode())
                            && invoice.getInvoiceTaxes().get(k).getValue() == itemTaxes.get(j).getValue()) {
                        invoice.getInvoiceTaxes().get(k).setAmount(round(invoice.getInvoiceTaxes().get(k).getAmount() + itemTaxes.get(j).getTaxesType().getTaxAmount(), 2));
                        //if in 2.parametresi kontrol edilmedi
                    }
                }
            }
        }
        return invoice;
    }

    public double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
