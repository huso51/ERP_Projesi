import { PaymentCheckbookItem } from './paymentCheckbookItem';

export class PaymentCheckbook {

    id: number;
    name: string;
    description: string;
    checkbookItems: PaymentCheckbookItem[];
    amount= 0;
    constructor() {
        this.checkbookItems = new Array<PaymentCheckbookItem>();
    }
}
