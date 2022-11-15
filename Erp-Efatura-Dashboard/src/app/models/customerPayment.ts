import { Tenant } from './tenant';
import { Customer } from './customer';
import { PaymentCash } from './paymentCash';
import { PaymentCheckbook } from './paymentCheckbook';
import { PaymentBill } from './paymentBill';

export class CustomerPayment {

    id: number;
    tenant: Tenant;
    customer: Customer;
    description: string;
    type: string;
    paymentCash: PaymentCash;
    paymentCheckbook: PaymentCheckbook;
    paymentBill: PaymentBill;
    debt: number;
    receivable: number;
    remaining: number;
    isPaid: boolean;
    createdAt: string;
}
