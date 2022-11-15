import { UomCode } from 'app/models/uomCode';
import { Currency } from 'app/models/currency';
import { TaxesType } from 'app/models/taxesType';
import { Category } from 'app/models/category';
import { Taxes } from 'app/models/taxes';
import { Storage } from 'app/models/storage';
import { Tenant } from 'app/models/tenant';

export class Item {
    id: string;
    name: string;
    description: string;
    categoryId: string;
    tenantId: string;
    tenant: Tenant;
    category: Category;
    storage: Storage;
    uomCode: UomCode;
    stock: number;
    price: number;
    buying: number;
    barcode: String;
    image: String;
    currency: Currency;
    itemTaxes: Taxes[]= [];
    created_at: string;

    constructor() {
        this.tenant = new Tenant();
        this.uomCode = new UomCode();
        this.category = new Category();
        this.currency = new Currency();
        this.uomCode = new UomCode();
        this.storage = new Storage();
    }
}
