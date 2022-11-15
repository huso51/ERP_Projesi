import { IMyDpOptions } from 'mydatepicker';

export class GlobalTexts {
    // public static rest_url = `https://erptest.java.com.tr:8443/ErpRest/webresources/`;
     public static rest_url = `https://erptestrestapi.java.com.tr/ErpRest/webresources/`;
    // public static rest_url = `https://gibtest.java.com.tr/ErpRest/webresources/`;
    // public static rest_url = `https://erprestapi.java.com.tr/ErpRest/webresources/`;
    // static rest_url = `http://192.168.1.25:8084/ErpRest/webresources/`;
    // public static rest_url = `https://apps.belgesakla.com/ErpRest/webresources/`;
    //   static rest_url = `https://efaturaws.java.com.tr/EfaturaRest/webresources/`;
    //   static rest_apiUrl = `https://efaturaws.java.com.tr/einvoice/api/`;
    //   static rest_url = `http://192.168.100.47:8084/EfaturaRest/webresources/`;
    //   static rest_apiUrl = `http://192.168.100.47:8084/einvoice/api/`;

    // static rest_url = `https://erptestrestapi.java.com.tr/ErpRest/webresources/`;
    static rest_apiUrl = `https://efaturaws.java.com.tr/einvoice/api/`;

    public static datepickerOptions: IMyDpOptions = {
        dateFormat: 'yyyy-mm-dd',
        dayLabels: { su: 'Pzr', mo: 'Pzt', tu: 'Sal', we: 'Çar', th: 'Per', fr: 'Cum', sa: 'Cts' },
        monthLabels: {
            1: 'Ocak', 2: 'Şubat', 3: 'Mart', 4: 'Nisan', 5: 'Mayıs',
            6: 'Haziran', 7: 'Temmuz', 8: 'Ağustos', 9: 'Eylül', 10: 'Ekim', 11: 'Kasım', 12: 'Aralık',
        },
        maxYear: new Date().getFullYear(),
        monthSelector: true,
        yearSelector: true,
      firstDayOfWeek: 'mo',
      sunHighlight: true,
        inline: false,
        todayBtnTxt: 'Bugün',

    };






    static invoiceTypes = [
        {
            id: 1,
            code: "SATIS",
            description: "SATIŞ FATURASI"
        },
        {
            id: 2,
            code: "IADE",
            description: "İADE FATURASI"
        },
        {
            id: 3,
            code: "ISTISNA",
            description: "İSTİSNA FATURASI"
        },
        {
            id: 4,
            code: "OZELMATRAH",
            description: "ÖZEL MATRAH FATURASI"
        },
        {
            id: 5,
            code: "TEVKIFAT",
            description: "TEVKİFAT FATURASI"
        }
    ];





    static invoiceWithholdings = [
        {
            id: 1,
            code: "601",
            value: 20,
            description: "2/10"
        },
        {
            id: 2,
            code: "602",
            value: 90,
            description: "9/10"
        },
        {
            id: 3,
            code: "603",
            value: 50,
            description: "5/10"
        }
    ];




    static invoiceExceptions = [
        {
            id: 3,
            code: "201",
            description: "17/1 Kültür ve Eğitim Amacı Taşıyan İşlemler"
        },
        {
            id: 4,
            code: "202",
            description: "17/2-a Sağlık, Çevre Ve Sosyal Yardım Amaçlı İşlemler"
        },
        {
            id: 5,
            code: "204",
            description: "17/2-c Yabancı Diplomatik Organ Ve Hayır Kurumlarının Yapacakları"
        },
        {
            id: 6,
            code: "205",
            description: "17/2-d Taşınmaz Kültür Varlıklarına İlişkin Teslimler ve Mimarlık Hizmetleri"
        },
        {
            id: 7,
            code: "206",
            description: "17/2-e Mesleki Kuruluşların İşlemleri"
        },
        {
            id: 8,
            code: "351",
            description: "Diğer"
        }
    ];



    static currencies = [
        {
            id: 1,
            code: "TRY",
            description: "TL"
        },
        {
            id: 2,
            code: "USD",
            description: "DOLAR"
        },
        {
            id: 3,
            code: "EUR",
            description: "EURO"
        }
    ];


    static invoiceScenarios = [
        {
            id: 1,
            code: "TEMELFATURA",
            description: "TEMEL FATURA"
        },
        {
            id: 2,
            code: "TICARIFATURA",
            description: "TİCARİ FATURA"
        },
        {
            id: 3,
            code: "YOLCUBERABERFATURA",
            description: "YOLCU BERABER FATURA"
        },
        {
            id: 4,
            code: "IHRACAT",
            description: "İHRACAT"
        },
        {
            id: 5,
            code: "EARSIVFATURA",
            description: "E-ARŞİV FATURA"
        }
    ];

    
    
    static uomCodes = [
        {
            id: 1,
            code: "C62",
            description: "ADET"
        },
        {
            id: 2,
            code: "KGM",
            description: "KİLOGRAM"
        },
        {
            id: 3,
            code: "BX",
            description: "KUTU"
        },
        {
            id: 4,
            code: "TNE",
            description: "TON"
        },
        {
            id: 5,
            code: "LTR",
            description: "LİTRE"
        },
        {
            id: 6,
            code: "GRM",
            description: "GRAM"
        },
        {
            id: 7,
            code: "MGM",
            description: "MİLİGRAM"
        },
        {
            id: 7,
            code: "NT",
            description: "NET TON"
        },
        {
            id: 7,
            code: "GT",
            description: "GROSS TON"
        },
        {
            id: 7,
            code: "MGM",
            description: "MİLİGRAM"
        }
    ];


}
