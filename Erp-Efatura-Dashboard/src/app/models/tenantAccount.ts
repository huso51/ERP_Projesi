import { Tenant } from './tenant';

export class TenantAccount {
    id: number;
    tenant: Tenant;
    no: string;
    name: string;
    description: string;
    amount: number;
    createdAt: string;
}
