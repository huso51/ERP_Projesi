import { PaymentBill } from './paymentBill';
import { TenantAccount } from './tenantAccount';

export class PaymentBillInstallment {

    id: number;
    paymentBill: PaymentBill;
    amount: number;
    isPaid: boolean;
    tenantAccount: TenantAccount;
    expiryDate: string;
}
