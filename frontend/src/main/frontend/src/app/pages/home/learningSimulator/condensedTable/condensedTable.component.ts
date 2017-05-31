import { Component, AfterViewInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LearningEngineService } from '../../learningengine.service';

@Component({
  selector: 'condensed-table',
  templateUrl: './condensedTable.html'
})
export class CondensedTable implements AfterViewInit {

  peopleTableData: Array<JSON>;
  lpid: any;

  constructor(private route: ActivatedRoute, private learningEngineService: LearningEngineService) {

  }

  ngAfterViewInit() {
    //alert("hi");
    this.route
      .params
      .subscribe(params => {
        // Defaults to 0 if no query param provided.
        this.lpid = params['id'] || '0';
         //console.log("initalized lpinstids: " + this.lpid)
      });
    this.learningEngineService.getoraclevalues(this.lpid).subscribe(response => {
      if (response.status == 'success') {
        //alert(JSON.stringify(response.oracledata));

        this.peopleTableData = response.oracledata;
      }
    });

    
  }

  public refreshTable(){
     this.route
      .params
      .subscribe(params => {
        // Defaults to 0 if no query param provided.
        this.lpid = params['id'] || '0';
         //console.log("initalized lpinstids: " + this.lpid)
      });
    this.learningEngineService.getoraclevalues(this.lpid).subscribe(response => {
      if (response.status == 'success') {
        //alert(JSON.stringify(response.oracledata));

        this.peopleTableData = response.oracledata;
      }
    });
  }

}
