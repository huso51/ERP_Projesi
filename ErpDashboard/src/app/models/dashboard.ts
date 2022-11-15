import { Item } from 'app/models/item';
import { Invoice } from 'app/models/invoice';

export class Dashboards {
    inboxCount: number;
    outboxCount: number;
    customerCount: number;
    item: Item[];
    itemCount: number;
    spendedCredits: number;
    remainingCredits: number;
    totalCredits: number;
    remainingExpireDate: number;
    inbox: Invoice[];
    outbox: Invoice[];

    constructor() {
        this.item = new Array<Item>();
        this.inbox = new Array<Invoice>();
        this.outbox = new Array<Invoice>();
    }
}
