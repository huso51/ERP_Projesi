import { Customer } from 'app/models/customer';
import { TenantPrefix } from 'app/models/tenantPrefix';

export class Tenant {
    id: string;
    name: string;
    owner: string;
    status: string;
    description: string;
    tenantType: string;
    email: string;
    ownerTenant: Tenant;
    addressName: string;
    cityId: string;
    districtId: string;
    postCode: string;
    phoneNumber: string;
    emailAddress: string;
    fax: string;
    fullAddress: string;
    tenantInfoId: number;
    tenantInfo: Customer;
    expireDate: string;
    expireCount: number;
    logo: string;
    signature: string;
    tenantPrefix: TenantPrefix[];

    constructor() {
        this.tenantInfo = new Customer();
        this.tenantPrefix = new Array<TenantPrefix>();
    }
}
