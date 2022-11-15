import { PaymentCheckbook } from './paymentCheckbook';
import { TenantAccount } from './tenantAccount';

export class PaymentCheckbookItem {

    id: number;
    PaymentCheckbook: PaymentCheckbook;
    amount: number;
    isGivenToCustomer: boolean;
    isPaid: boolean;
    tenantAccount: TenantAccount;
    checkbookDate: string;
    paymentDate: string;
    serialNo: string;
    bankName: string;
}
