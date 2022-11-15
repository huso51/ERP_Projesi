import { PaymentBillInstallment } from './paymentBillInstallment';

export class PaymentBill {

    id: number;
    name: string;
    description: string;
    amount: number;
    billDate: string;
    createdAt: string;
    billInstallments: PaymentBillInstallment[];

    constructor() {
        this.billInstallments = new Array<PaymentBillInstallment>();
    }
}
