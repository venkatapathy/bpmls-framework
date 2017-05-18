import { Type, Component, ViewChild, ViewContainerRef, AfterViewInit } from '@angular/core';
import { LearningEngineService } from '../learningengine.service';
import { AdHocComponentFactoryCreator } from './adhoc-component-factory.service';
import { ActivatedRoute } from '@angular/router';
import { NgForm } from '@angular/forms';
declare var introJs: any;

@Component({
  selector: 'learning-simulator',
  templateUrl: './learningSimulator.html',
})
export class LearningSimulator implements AfterViewInit {
  private lpid: string;

  public checkboxModel = [{
    name: 'Check 1',
    checked: false,
    class: 'col-md-4'
  }, {
    name: 'Check 2',
    checked: true,
    class: 'col-md-4'
  }, {
    name: 'Check 3',
    checked: false,
    class: 'col-md-4'
  }];
  @ViewChild('taskFormContainer', { read: ViewContainerRef }) parent: ViewContainerRef;
  isChecked: boolean = false;
  constructor(private route: ActivatedRoute, private adHocComponentFactoryCreator: AdHocComponentFactoryCreator, private learningEngineService: LearningEngineService) {
  }

  private createDynamicComponentwithModel(taskform: string, modelJs: JSON, prompt: JSON, simulatorComponent: LearningSimulator): Type<any> {
    @Component({
      template: taskform
    })
    class InsertedComponent {
      learningForm = modelJs;
      constructor() {

        //alert(JSON.stringify(this.learningForm));
      }
      private completeLearning(f) {

        //alert(JSON.stringify(f.value));
        simulatorComponent.completeLearning(JSON.stringify(f.value));

      }
      startIntro() {
        var intro = introJs();
        intro.setOptions(prompt);

        intro.start();
      }

      startLs(lpinstid) {
        simulatorComponent.startLearningScenario(lpinstid);
        //alert("hi");
      }
    }

    return InsertedComponent;
  }

  private createDynamicComponent(taskform: string, prompt: JSON, simulatorComponent: LearningSimulator): Type<any> {
    @Component({
      template: taskform
    })
    class InsertedComponent {

      completeLearning(learningForm) {
        alert(JSON.stringify(learningForm.getRawValue()));
        simulatorComponent.completeLearning(JSON.stringify(learningForm.value));

      }
      startIntro() {
        var intro = introJs();
        intro.setOptions(prompt);

        intro.start();
      }

      startLs(lpinstid) {
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
        this.lpid = params['id'] || '0';
        console.log("initalized lpinstid: " + this.lpid)
      });

    //this.simulatorService.getcurrentlearningtask('learningscenario1','7').subscribe(response=> {this.dataContainer.nativeElement.innerHTML =response; console.log(this.taskform);});
    this.loadForm();

  }



  loadForm() {
    //try to get the model
    this.learningEngineService.getcurrenttaskmodel(this.lpid).subscribe(response => {
      if (response.status == "success") {
        //alert(JSON.stringify(response.learningpath));
        let model = response.formmodel;
        this.learningEngineService.getcurrentlearningtask(this.lpid).subscribe(response => {
          if (response.status == "success") {
            let prompt = JSON.parse("{\"steps\": [{ \"intro\": \"welcome to case \"},{ \"intro\": \"In this scenario you will open a case \"}]}");
            let component = this.createDynamicComponentwithModel(response.htmlform, model, prompt, this);
            //console.log(response);
            let componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
            this.parent.clear();
            let componentRef = this.parent.createComponent(componentFactory);

            componentRef.changeDetectorRef.detectChanges();
          }else{
            //TODO: better error handling
            alert(response.errMsg)

          }

        });
      } else {
        //TODO: Better error handling
        alert(response.errMsg);

      }
      /*else {
        this.learningEngineService.getcurrentlearningtask(this.lpid).subscribe(response => {
          let prompt = JSON.parse("{\"steps\": [{ \"intro\": \"welcome to case \"},{ \"intro\": \"In this scenario you will open a case \"}]}");
          let component = this.createDynamicComponent(response, prompt, this);
          console.log(response);
          let componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
          this.parent.clear();
          let componentRef = this.parent.createComponent(componentFactory);

          componentRef.changeDetectorRef.detectChanges();
        }
        );
      }*/
    });

  }

  completeLearning(learningForm: string) {

    //submit the form
    this.learningEngineService.completeLearningTask(this.lpid, "1", learningForm).subscribe(response => {
      //get the response after submitting the task
      console.log(response.status);
      if (response.status=="success") {
        console.log("Setting alter mesage");
        this.learningEngineService.publishAlertMsg("Completed task")
        this.loadForm();

      } else if (response.status=="error") {
        alert(JSON.stringify(response.errMsg));
      }
    });


  }

  startLearningScenario(lpinstid: string) {
    this.learningEngineService.startalearningscenario(this.lpid, lpinstid).subscribe(response => {
      if (response.status == "success") {
        this.loadForm();
      } else {
        //TODO: Better Error Handling
        alert(response.errMsg);
      }
    });
  }
}