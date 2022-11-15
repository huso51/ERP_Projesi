export class TenantPrefix {
    id: number;
    tenantId: string;
    name: string;
    code: string;
    description: string;
    createdAt: string;
    isEditing: boolean;

    constructor() {
        this.code = 'ERP';
        this.isEditing = false;
    }
}
