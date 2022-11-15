import { UomCode } from 'app/models/uomCode';
import { Currency } from 'app/models/currency';
import { TaxesType } from 'app/models/taxesType';
import { Category } from 'app/models/category';
import { Taxes } from 'app/models/taxes';

export class Item {
    id: string;
    name: string;
    description: string;
    categoryId: string;
    tenantId: string;
    category: Category;
    uomCode: UomCode;
    stock: number;
    price: number;
    currency: Currency;
    itemTaxes: Taxes[]= [];
    createdAt: string;

    constructor() {
        this.uomCode = new UomCode();
        this.category = new Category();
        this.currency = new Currency();
        this.uomCode = new UomCode();
    }
}
