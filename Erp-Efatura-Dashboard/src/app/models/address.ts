import { Neighborhood } from 'app/models/neighborhood';
import { City } from 'app/models/city';
import { District } from 'app/models/district';

export class Address {
    public id: number;
    public addressName: string;
    public cityId: number;
    public districtId: number;
    public neighborhoodId: number;
    public phoneNumber: string;
    public email: string;
    public fax: string;
    public fullAddress: string;
    public customerId: number;
    public neighborhood: Neighborhood;
    public city: City;
    public district: District;
    public isDefaultAddress: boolean;
    
    constructor( ) {
        this.addressName = '';
        this.neighborhood = new Neighborhood();
        this.district = new District();
        this.city = new City();
    }
}

