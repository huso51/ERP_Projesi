import { Tenant } from 'app/models/tenant';
import { UserRole } from 'app/models/userRole';

export class TenantUsers {
    constructor( public id: number,
    public tenantId: string,
    public userId: string,
    public userRolesId: string,
    public userRole: UserRole,
    public tenant: Tenant,
    ) {
    }
}
