export class GibInbox {
    uuid: string;
    invoiceId: string;
    profile: string;
    typeCode: string;
    issueDate: string;
    issueTime: string;
    envelopeId: string;
    envelopeDate: string;
    date: string;
    partyId: string;
    partyName: string;
    senderPartyId: string;
    senderPartyName: string;
    receiverPartyId: string;
    receiverPartyName: string;
    taxSchemeName: string;
    payableAmount: string;
    currency: string;
    statusCode: string;
    statusDescription: string;
    applicationResponseId: string;
    applicationResponse: string;
    applicationResponseDate: string;
    applicationResponseDescription: string;
    isCancelled: string;
    cancelDate: string;
    cancelReason: string;
    isReaded: boolean;
    isChecked: boolean;
    isReported: boolean;

    constructor() {
        this.isChecked = false;
    }
}
