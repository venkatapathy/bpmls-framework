import {Component} from '@angular/core';

import {LsCompletionChartService} from './lsCompletionChart.service';
import * as Chart from 'chart.js';

@Component({
  selector: 'lscompletion-chart',
  templateUrl: './lsCompletionChart.html',
  styleUrls: ['./lsCompletionChart.scss']
})

// TODO: move chart.js to it's own component
export class LsCompletionChart {

  public doughnutData: Array<Object>;

  constructor(private trafficChartService:LsCompletionChartService) {
    this.doughnutData = trafficChartService.getData();
  }

  ngAfterViewInit() {
    this._loadDoughnutCharts();
  }

  private _loadDoughnutCharts() {
    let el = jQuery('.chart-area').get(0) as HTMLCanvasElement;
    new Chart(el.getContext('2d')).Doughnut(this.doughnutData, {
      segmentShowStroke: false,
      percentageInnerCutout : 64,
      responsive: true
    });
  }
}
