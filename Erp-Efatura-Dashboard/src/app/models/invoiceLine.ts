import { Item } from 'app/models/item';
import { UomCode } from 'app/models/uomCode';
import { Currency } from 'app/models/currency';
import { InvoiceLineTaxes } from 'app/models/invoiceLineTaxes';

export class InvoiceLine {
  constructor(
    public id: number,
    public invoiceId: number,
    public itemId: number,
    public item: Item,
    public quantity: number,
    public discountAmount: number,
    public price: number,
    public lastPrice: number,
    public invoiceLineTaxes: InvoiceLineTaxes[]= [],
    public exceptionReason: string,
    public currencyMultiplier: number,
      ) { 
        this.discountAmount = 0;
        item = new Item();
        price = 0;
        this.currencyMultiplier = 1;
      }    
}
