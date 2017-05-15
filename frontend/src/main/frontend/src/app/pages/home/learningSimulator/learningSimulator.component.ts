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
  
   private lpinstid: string;
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
    }

    return InsertedComponent;
  }



  ngAfterViewInit() {
    this.route
      .queryParams
      .subscribe(params => {
        // Defaults to 0 if no query param provided.
        this.lpinstid = params['id'] || '0' ;
        console.log("initalized lpinstid: "+this.lpinstid)
      });
      
    //this.simulatorService.getcurrentlearningtask('learningscenario1','7').subscribe(response=> {this.dataContainer.nativeElement.innerHTML =response; console.log(this.taskform);});
    this.loadForm();

  }



  loadForm() {
    this.learningEngineService.getcurrentlearningtask(this.lpinstid).subscribe(response => {
      let prompt = JSON.parse("{\"steps\": [{ \"intro\": \"welcome to case \"}]}");
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
    this.learningEngineService.completeLearningTask(this.lpinstid, learningForm).subscribe(response => {
      //get the response after submitting the task
      console.log(response.status);
      if (response.status === "completed") {
        console.log("Setting alter mesage");
        this.learningEngineService.publishAlertMsg("Completed task")
        this.loadForm();

      }
    });

    //send the values to completelearningtask

    //get the reply and check the status, display the status and refresh the form
  }
}