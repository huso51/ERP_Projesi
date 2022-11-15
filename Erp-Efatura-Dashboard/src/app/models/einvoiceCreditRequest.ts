import { EInvoiceErp } from './einvoiceErp';
import { EInvoiceMukellef } from './einvoiceMukellef';

export class EInvoiceCreditRequest {

    id: number;
    admin: EInvoiceErp;
    user: EInvoiceMukellef;
    amount: number;
    description: string;
    confirmed: boolean;
    confirmedAt: string;
    createdAt: string;

    constructor() {

    }
}
