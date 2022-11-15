export class InvoiceLineTaxes { 
  constructor(
    public id: number,
    public invoiceId: number,
    public code: string,
    public value: number,
    public invoiceLineId: number,
      ) { 
      }    
}
