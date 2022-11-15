import { Tenant } from 'app/models/tenant';
import { Customer } from 'app/models/customer';
import { Currency } from 'app/models/currency';
import { InvoiceLine } from 'app/models/invoiceLine';
import { InvoiceLineTaxes } from 'app/models/invoiceLineTaxes';
import { InvoiceType } from 'app/models/invoiceType';
import { InvoiceWithholding } from 'app/models/invoiceWithholding';
import { InvoiceException } from 'app/models/invoiceException';
import { InvoiceScenario } from "app/models/invoiceScenario";

export class Invoice {
    id: number;
    tenantId: string;
    tenant: Tenant;
    sn1: string;
    sn2: string;
    sn3: string;
    currencyId: number;
    currency: Currency;
    createdAt: string;
    orderNo: string;
    waybillNumber: string;
    waybillDate: string;
    uuid: string;
    customerId: string;
    customer: Customer;
    subTotal: number;
    discountTotal: number;
    grossTotal: number;
    taxesTotal: number;
    priceTotal: number;
    invoiceType: InvoiceType;
    description: string;
    invoiceWithholdingId: number;
    invoiceWithholding: InvoiceWithholding;
    invoiceLine: InvoiceLine[]= [];
    confirmed: boolean;
    orderDate: string;
    invoiceExceptionId: number;
    invoiceException: InvoiceException;
    isChecked: boolean;
    isComingInvoice: boolean;
    receiverIdentifier: string;
    invoiceScenario: InvoiceScenario;
    note: string;
    constructor() {
        this.invoiceScenario = new InvoiceScenario();
        this.invoiceType = new InvoiceType(0, '', '');
        this.customer = new Customer();
        this.currency = new Currency();
        this.subTotal = 0;
        this.grossTotal = 0;
        this.priceTotal = 0;
        this.taxesTotal = 0;
        this.discountTotal = 0;
        this.isChecked = false;
    }
}
