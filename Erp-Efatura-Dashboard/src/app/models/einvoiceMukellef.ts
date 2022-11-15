import { EInvoiceErp } from './einvoiceErp';
import { EInvoiceStatistic } from './einvoiceStatistic';

export class EInvoiceMukellef {
    admin: EInvoiceErp;

    confirmed: boolean;
    createdAt: string
    credit: number;
    email: string;
    gb: string;
    id: number;
    isAdmin: boolean;
    isSuperAdmin: boolean;
    isUser: boolean;
    name: string;
    ownerId: number;
    password: string
    pk: string;
    statistic: EInvoiceStatistic;
    vkn: string;
    constructor() {

    }
}
