import { Type, Component, ViewChild, ViewContainerRef, AfterViewInit } from '@angular/core';
import {LearningEngineService} from '../learningengine.service';
import {AdHocComponentFactoryCreator} from './adhoc-component-factory.service';
import { ActivatedRoute } from '@angular/router';

declare var introJs:any;

@Component({
  selector: 'learning-simulator',
  templateUrl: './learningSimulator.html',
})
export class LearningSimulator implements AfterViewInit {
   private lpid:string;
  
  @ViewChild('taskFormContainer', { read: ViewContainerRef }) parent: ViewContainerRef;
  isChecked: boolean = false;
  constructor(private route:ActivatedRoute, private adHocComponentFactoryCreator: AdHocComponentFactoryCreator,private learningEngineService:LearningEngineService) {
  }

  private createDynamicComponent(taskform:string,prompt:JSON,simulatorComponent:LearningSimulator): Type<any> {
    @Component({
      template: taskform
    })
    class InsertedComponent {

      completeLearning(learningForm) {
        simulatorComponent.completeLearning(JSON.stringify(learningForm.value));
        //alert("hi");
      }
      startIntro() {
        var intro = introJs();
        intro.setOptions(prompt);

        intro.start();
      }

      startLs(lpinstid){
        simulatorComponent.startLearningScenario(lpinstid);
        //alert("hi");
      }
    }

    return InsertedComponent;
  }



  ngAfterViewInit() {
    this.route
      .params
      .subscribe(params => {
        // Defaults to 0 if no query param provided.
        this.lpid = params['id'] || '0' ;
        console.log("initalized lpinstid: "+this.lpid)
      });
      
    //this.simulatorService.getcurrentlearningtask('learningscenario1','7').subscribe(response=> {this.dataContainer.nativeElement.innerHTML =response; console.log(this.taskform);});
    this.loadForm();

  }



  loadForm() {
    this.learningEngineService.getcurrentlearningtask(this.lpid).subscribe(response => {
      let prompt = JSON.parse("{\"steps\": [{ \"intro\": \"welcome to case \"},{ \"intro\": \"In this scenario you will open a case \"}]}");
      let component = this.createDynamicComponent(response, prompt, this);
      console.log(response);
      let componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
      this.parent.clear();
      let componentRef = this.parent.createComponent(componentFactory);

      componentRef.changeDetectorRef.detectChanges();
    });
  }
  completeLearning(learningForm: string) {

    //submit the form
    this.learningEngineService.completeLearningTask(this.lpid, learningForm).subscribe(response => {
      //get the response after submitting the task
      console.log(response.status);
      if (response.status === "completed") {
        console.log("Setting alter mesage");
        this.learningEngineService.publishAlertMsg("Completed task")
        this.loadForm();

      }
    });

    
  }

  startLearningScenario(lpinstid: string){
    this.learningEngineService.startalearningscenario(this.lpid,lpinstid).subscribe(response =>{
      if(response.success){
         this.loadForm();
      }else{
        console.log(response);
      }
    });
    }
}