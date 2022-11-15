import { TenantUsers } from 'app/models/tenantUsers';
import { UserRole } from 'app/models/userRole';
import { Tenant } from 'app/models/tenant';

export class User {
    id: string;
    name: string;
    email: string;
    password: string;
    status: string;
    permissions: string;
    description: string;
    defaultTenantId: string;
    rememberToken: string;
    tenantUsers: TenantUsers;
    userInfo: {
        id: string;
        name: string;
        email: string;
        status: string;
        defaultTenantUserId: string;
        tenantUsers: {
            userId: string;
            userRole: {
                user: string;
                customer: string;
                tenant: string;
                item: string;
                invoice: string;
                isSuperAdmin: string;
            };
        }
    };

    constructor() {
        this.tenantUsers = new TenantUsers(0, '', '', '', 
            new UserRole(0, 'x', 'x', 'x', 'x', 'x', false, false), new Tenant());
    }
}
