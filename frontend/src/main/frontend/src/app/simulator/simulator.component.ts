import { Component, AfterViewInit , ViewChild, ElementRef } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import {SimulatorService} from './simulator.service'
@Component({
    selector: 'simulator',
    templateUrl: 'simulator.component.html'
})

export class SimulatorComponent implements AfterViewInit{
    @ViewChild('taskFormContainer') dataContainer: ElementRef;
    taskform:any="";
    constructor(private simulatorService:SimulatorService) { }

    ngAfterViewInit() {
        // reset login status
        this.simulatorService.getcurrentlearningtask('learningscenario1','7').subscribe(response=> {this.dataContainer.nativeElement.innerHTML =response; console.log(this.taskform);});
        
        
    }

    completeLearning() {
        console.log("hi");
        alert("hi");
    }
     onSubmitTemplateBased() {
        console.log("hello");
    }
}