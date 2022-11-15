import { Tenant } from 'app/models/tenant';
import { UserRole } from 'app/models/userRole';

export class TenantUser {
    constructor(
    id: number,
    tenantId: string,
    userId: string,
    userRolesId: string,
    userRole: UserRole,
    tenant: Tenant) {

    }

}