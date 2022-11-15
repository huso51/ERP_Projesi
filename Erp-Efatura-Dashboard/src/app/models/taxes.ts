import { TaxesType } from 'app/models/taxesType';

export class Taxes { 
  constructor(
    public id: number,
    public itemId: number,
    public code: string,
    public value: number,
    public taxesType: TaxesType,
      ) { 
      }    
}
