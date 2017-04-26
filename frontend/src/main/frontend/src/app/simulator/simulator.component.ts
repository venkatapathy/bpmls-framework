import { NgZone,Type, Component, AfterViewInit , ViewChild, ElementRef, ViewContainerRef, ComponentFactoryResolver } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import {SimulatorService} from './simulator.service';
import { DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {AdHocComponentFactoryCreator} from './adhoc-component-factory.service';
import {AlertsNMsgsComponent} from '.././alertsnmsgs/alertsnmsgs.component'
declare var introJs:any;
@Component({
    selector: 'simulator',
    templateUrl: 'simulator.component.html'
})

export class SimulatorComponent implements AfterViewInit{
     private lsinstid:string;
     private pinstid:string;


    
     @ViewChild('taskFormContainer', {read: ViewContainerRef}) parent: ViewContainerRef;
   
    constructor(private zone: NgZone,private simulatorService:SimulatorService,private _sanitizer: DomSanitizer, private adHocComponentFactoryCreator: AdHocComponentFactoryCreator) {
        this.lsinstid="1";
        this.pinstid="7";
     }

     private createDynamicComponent(taskform:string,prompt:JSON,simulatorComponent:SimulatorComponent): Type<any> {
    @Component({
      template: taskform
    })
    class InsertedComponent { 
        
        completeLearning(learningForm) {
            simulatorComponent.completeLearning(JSON.stringify(learningForm.value));
        //alert("hi");
    }
    startIntro(){
        var intro = introJs();
          intro.setOptions(prompt);

          intro.start();
      }
    }
    
    return InsertedComponent;
  }

    ngAfterViewInit() {
        
        //this.simulatorService.getcurrentlearningtask('learningscenario1','7').subscribe(response=> {this.dataContainer.nativeElement.innerHTML =response; console.log(this.taskform);});
        this.loadForm();
        
    }

   

    loadForm(){
        this.simulatorService.getcurrentlearningtask(this.lsinstid).subscribe(response=> {
            let prompt=JSON.parse("{\"steps\": [{ \"intro\": \"welcome to case \"}]}");
             let component = this.createDynamicComponent(response,prompt,this);
             let componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
             let componentRef=this.parent.createComponent(componentFactory);
             componentRef.changeDetectorRef.detectChanges();
        });
    }
    completeLearning(learningForm:string) {
        
        //submit the form
        this.simulatorService.completeLearningTask(this.lsinstid,learningForm).subscribe(response =>{
            //get the response after submitting the task
            console.log(response.status);
            if(response.status==="completed"){
                console.log("Setting alter mesage");
                this.simulatorService.publishAlertMsg("Completed task")
               this.loadForm();
                
            }
        });
         
        //send the values to completelearningtask

        //get the reply and check the status, display the status and refresh the form
    }
     onSubmitTemplateBased() {
        console.log("hello");
    }
}