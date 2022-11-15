import { Customer } from './customer';
import { User } from './user';
import { TenantAccount } from './tenantAccount';

export class AccountActivity {
    id: number;
    account: TenantAccount;
    user: User;
    customer: Customer;
    process: string;
    description: string;
    debt: number;            // borç hanesi
    receivable: number;      // alcak hanesi
    remaining: number;       // hesapta kalan hanesi
    createdAt: string;
}
