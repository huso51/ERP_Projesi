import { Tenant } from 'app/models/tenant';

export class Storage {  
    id: number;
    tenantsId: string;
    tenant: Tenant;
    name: string;
    create_date: string;
    address: string;
    description: string;

    constructor() {
        this.tenant = new Tenant();
    }
}
