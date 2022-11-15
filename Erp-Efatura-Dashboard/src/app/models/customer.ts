import { Address } from 'app/models/address';
import { CustomerType } from 'app/models/customerType';

export class Customer {
    id: number;
    name: string;
    surname: string;
    appellation: string;
    fullAppellation: string;
    customerTypeId: number;
    taxAdministration: string;
    tc: string;
    tradeRegisterNumber: string;
    mersisNumber: string;
    basicDiscount: number;
    creditLimit: number;
    isAssent: boolean;
    tenantId: string;
    customerType: CustomerType;
    createdAt: string;
    confirmedAt: string;
    defaultAddressId: string;
    address: Address[];
    isEfaturaUser: boolean;
    senderIdentifier: string;
    remainder: number;
    
    constructor() {
        this.customerType = new CustomerType(0, '', '');
        this.address = new Array<Address>();
        this.isAssent = true;
        this.basicDiscount = 0;
        this.tc = String();
    }
}

