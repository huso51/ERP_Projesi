import { TenantAccount } from './tenantAccount';

export class PaymentCash {

    id: number;
    name: string;
    description: string;
    isPayingToCustomer = false;
    tenantAccount: TenantAccount;
    amount: number;
    createdAt: string;
    paymentDate: string;

    constructor() {
        this.isPayingToCustomer = false;
    }
}
