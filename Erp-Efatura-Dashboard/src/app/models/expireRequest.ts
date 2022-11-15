export class ExpireRequest {
    id: number;
    tenantId: string;
    tenantName: string;
    ownerId: string;
    ownerName: string;
    amount: number;
    description: string;
    confirmed: boolean;
    createdAt: string;

    constructor() {

    }
}
