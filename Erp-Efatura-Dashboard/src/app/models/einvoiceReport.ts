export class EInvoiceReport {
    uuid: string;
    periodUuid: string;
    vknTckn: string;
    content: Array<ByteString>;
    partNo: number;
    issueDateTime: string;
    periodStart: string;
    periodEnd: string;
    partStart: string;
    partEnd: string;
    size: number;
    isSended: boolean;
    statusDescription: string;

    constructor() {

    }
}
