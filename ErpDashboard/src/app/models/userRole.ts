export class UserRole {
    constructor(
    public id: number,
    public user: string,
    public customer: string,
    public tenant: string,
    public item: string,
    public invoice: string,
    public isTenantAdmin: boolean,
    public isSuperAdmin: boolean) {
        this.id = 0;
        this.user = 'x';
        this.customer = 'x';
        this.item = 'x';
        this.invoice = 'x';
        this.isTenantAdmin = false;
        this.isSuperAdmin = false;
    }

}
