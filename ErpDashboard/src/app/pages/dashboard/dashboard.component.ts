import { Component, OnInit, ViewChild } from '@angular/core';
import { Dashboards } from 'app/models/dashboard';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { GlobalTexts } from 'globals/globaltexts';
import { FetchService } from 'app/pages/fetch.service';
import { BaThemeConfigProvider, colorHelper, layoutPaths } from 'app/theme';
import * as Chart from 'chart.js';
import { BaseChartDirective } from "ng2-charts";

@Component({
  selector: 'dashboard',
  styleUrls: ['./dashboard.scss'],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  @ViewChild(BaseChartDirective) _chart: BaseChartDirective;
  public charts: Object[];
  public dashboard: Dashboards;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  public doughnutData: Object[];
  public lineChartData: any[] = [
    [0],
    [0],
  ];
  public lineChartLabels:any[] = ['Ocak', 'Şubat', 'Mart', 'Nisan', 'Mayıs', 'Haziran', 'Temmuz'];
  public lineChartType: string = 'line';
 
  public randomizeType(): void {
    this.lineChartType = this.lineChartType === 'line' ? 'bar' : 'line';
  }
 
  public chartClicked(e: any): void {
    console.log(e);
  }
 
  public chartHovered(e: any): void {
    console.log(e);
  }
  constructor(private newService: FetchService, private _baConfig: BaThemeConfigProvider) {
    this.charts = new Array<Object>();
    this.doughnutData = new Array<Object>();
    this.dashboard = new Dashboards();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    this.getDashboardValues();
  }
  ngAfterViewInit() {
    this._loadDoughnutCharts();
    // this.setValues();
  }
  
  public getDashboardValues() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getDashboard`, body, this.options).
      subscribe(
      (data) => {
        this.dashboard = data.data;
        this._updatePieCharts();
        this.setValues();
        this.setLineChart();
      },
    );
  }

  public _updatePieCharts() {
    const pieColor = this._baConfig.get().colors.custom.dashboardPieChart;
    this.charts = [
      {
        color: pieColor,
        description: 'Gelen Faturalar',
        stats: this.dashboard.inbox.length,
        icon: 'fa fa-calculator',
      }, {
        color: pieColor,
        description: 'Giden Faturalar',
        stats: `${this.dashboard.outbox.length}`,
        icon: 'fa fa-calculator',
      }, {
        color: pieColor,
        description: 'Müşteri Sayısı',
        stats: this.dashboard.customerCount,
        icon: 'fa fa-id-card-o',
      }, {
        color: pieColor,
        description: 'Kalan Süre(Gün)',
        stats: `${this.dashboard.remainingExpireDate}`,
        icon: 'fa fa-calendar-o',
      }, {
        color: pieColor,
        description: 'Kontör',
        stats: `0`,
        icon: 'fa fa-gg-circle',
      },
    ];
  }
  public setValues() {
    const dashboardColors = this._baConfig.get().colors.dashboard;
    const sum: number = this.calculateItems();
    const fourSum: number = this.calculateFirstFourItems();
    this.doughnutData = [];
    if (this.dashboard.item.length > 0) {
      for (const item of this.dashboard.item) {
        this.doughnutData.push({
          value: item.stock,
          color: 
        `rgb(${Math.floor(Math.random() * 255)},${Math.floor(Math.random() * 255)},${Math.floor(Math.random() * 255)}`,
          highlight: colorHelper.shade(dashboardColors.white, 15),
          label: item.name,
          percentage: (item.stock / sum * 100).toFixed(3),
          order: 1,
        });
      }
      /*this.doughnutData = [
        {
          value: this.dashboard.item[0].stock,
          color: dashboardColors.white,
          highlight: colorHelper.shade(dashboardColors.white, 15),
          label: this.dashboard.item[0].name,
          percentage: (this.dashboard.item[0].stock / sum * 100).toFixed(3),
          order: 1,
        }, {
          value: this.dashboard.item[1].stock,
          color: dashboardColors.gossip,
          highlight: colorHelper.shade(dashboardColors.gossip, 15),
          label: this.dashboard.item[1].name,
          percentage: (this.dashboard.item[1].stock / sum * 100).toFixed(3),
          order: 4,
        }, {
          value: this.dashboard.item[2].stock,
          color: dashboardColors.silverTree,
          highlight: colorHelper.shade(dashboardColors.silverTree, 15),
          label: this.dashboard.item[2].name,
          percentage: (this.dashboard.item[2].stock / sum * 100).toFixed(3),
          order: 3,
        }, {
          value: this.dashboard.item[3].stock,
          color: dashboardColors.surfieGreen,
          highlight: colorHelper.shade(dashboardColors.surfieGreen, 15),
          label: this.dashboard.item[3].name,
          percentage: (this.dashboard.item[3].stock / sum * 100).toFixed(3),
          order: 2,
        }, {
          value: fourSum,
          color: dashboardColors.blueStone,
          highlight: colorHelper.shade(dashboardColors.blueStone, 15),
          label: 'Diğer',
          percentage: (fourSum / sum * 100).toFixed(3),
          order: 0,
        },
      ];*/
    }
    
    this._loadDoughnutCharts();
  }

  calculateItems(): number {
    let sum: number = 0;
    for (const item of this.dashboard.item) {
      sum += item.stock;
    }
    return sum;
  }
  calculateFirstFourItems(): number {
    let sum: number = 0;
    let i = 0;
    for (const item of this.dashboard.item) {
      if (i < 4) {
        sum += item.stock;
      }
      i++;
    }
    return sum;
  }

  private _loadDoughnutCharts() {
    const el = jQuery('.chart-area').get(0) as HTMLCanvasElement;
    new Chart(el.getContext('2d')).Doughnut(this.doughnutData, {
      segmentShowStroke: false,
      percentageInnerCutout: 64,
      responsive: true,
    });
  }

  setLineChart() {
    const monthNames = ['Ocak', 'Şubat', 'Mart', 'Nisan', 'Mayıs', 'Haziran',
  'Temmuz', 'Ağustos', 'Eylül', 'Ekim', 'Kasım', 'Aralık',
];

    const layoutColors = this._baConfig.get().colors;
    const graphColor = this._baConfig.get().colors.custom.dashboardLineChart;
    const chartDates: Object[] = new Array<Object>();
    this.lineChartData = new Array<any>();
    let tempChartData = new Array<any>();
    this.lineChartLabels = new Array<any>();
    for (const item of this.dashboard.inbox) {
      const date = new Date(item.createdAt);
      tempChartData.push(item.priceTotal);
      this.lineChartLabels.push(monthNames[date.getMonth()]);
    }
    this.lineChartData.push(tempChartData);
    tempChartData = new Array<any>();
    for (const item of this.dashboard.outbox) {
      const date = new Date(item.createdAt);
      tempChartData.push(item.priceTotal);
      this.lineChartLabels.push(monthNames[date.getMonth()]);
    }
    this.lineChartData.push(tempChartData);
    this.lineChartType = 'line';
    this.forceChartRefresh();
  }
  forceChartRefresh() {
        setTimeout(() => {
            (<any>this._chart).refresh();
        }, 10);
    }

}
