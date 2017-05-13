import { Type, Component, ViewChild, ViewContainerRef, AfterViewInit } from '@angular/core';
import {SimulatorService} from './simulator.service';
import {AdHocComponentFactoryCreator} from './adhoc-component-factory.service';
declare var introJs:any;

@Component({
  selector: 'learning-simulator',
  templateUrl: './learningSimulator.html',
})
export class LearningSimulator implements AfterViewInit {
  private lsinstid: string;
  @ViewChild('taskFormContainer', { read: ViewContainerRef }) parent: ViewContainerRef;
  isChecked: boolean = false;
  constructor(private adHocComponentFactoryCreator: AdHocComponentFactoryCreator,private simulatorService:SimulatorService) {
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

    //this.simulatorService.getcurrentlearningtask('learningscenario1','7').subscribe(response=> {this.dataContainer.nativeElement.innerHTML =response; console.log(this.taskform);});
    this.loadForm();

  }



  loadForm() {
    this.simulatorService.getcurrentlearningtask(this.lsinstid).subscribe(response => {
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
    this.simulatorService.completeLearningTask(this.lsinstid, learningForm).subscribe(response => {
      //get the response after submitting the task
      console.log(response.status);
      if (response.status === "completed") {
        console.log("Setting alter mesage");
        this.simulatorService.publishAlertMsg("Completed task")
        this.loadForm();

      }
    });

    //send the values to completelearningtask

    //get the reply and check the status, display the status and refresh the form
  }
}