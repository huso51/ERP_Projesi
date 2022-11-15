export class GlobalTexts {
    // public static rest_url = `https://erptest.java.com.tr:8443/ErpRest/webresources/`;
     public static rest_url = `https://gibtest.java.com.tr/ErpRest/webresources/`;
    // public static rest_url = `https://restapi.java.com.tr/ErpRest/webresources/`;
    // public static rest_url = `http://192.168.100.66:8084/ErpRest/webresources/`;
    // public static rest_url = `https://apps.belgesakla.com/ErpRest/webresources/`;

    public static datepickerOptions = {
        dateFormat: 'yyyy-mm-dd',
        dayLabels: { su: 'Pzr', mo: 'Pzt', tu: 'Sal', we: 'Çar', th: 'Per', fr: 'Cum', sa: 'Cts' },
        monthLabels: {
            1: 'Ocak', 2: 'Şubat', 3: 'Mart', 4: 'Nisan', 5: 'Mayıs',
            6: 'Haziran', 7: 'Temmuz', 8: 'Ağustos', 9: 'Eylül', 10: 'Ekim', 11: 'Kasım', 12: 'Aralık'
        },
        maxYear: 2017,
        monthSelector: true,
        todayBtnTxt: 'Bugün',
    };

}
