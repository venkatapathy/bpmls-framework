import { Inject,Type, Component, ViewChild, ViewContainerRef, AfterViewInit } from '@angular/core';
import { LearningEngineService } from '../learningengine.service';
import { AdHocComponentFactoryCreator } from './adhoc-component-factory.service';
import { ActivatedRoute } from '@angular/router';
import { NgForm } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModal } from '../../components/default-modal/default-modal.component';
import { Router } from '@angular/router';
import { WindowRefService } from '../window.ref.service';
import { Http } from '@angular/http';
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
    class: 'col-md-4',
  }, {
    name: 'Check 2',
    checked: true,
    class: 'col-md-4',
  }, {
    name: 'Check 3',
    checked: false,
    class: 'col-md-4',
  }];
  @ViewChild('taskFormContainer', { read: ViewContainerRef }) parent: ViewContainerRef;
  isChecked: boolean = false;
  BpmnViewer:any;
  viewer:any;
  private _window: Window;
  constructor(private http:Http,windowRef: WindowRefService,private router: Router, private modalService: NgbModal, private route: ActivatedRoute, private adHocComponentFactoryCreator: AdHocComponentFactoryCreator, private learningEngineService: LearningEngineService) {
    this._window = windowRef.nativeWindow;
    this.BpmnViewer=(<any>this._window).BpmnJS;
    
  }

  handleError(err: any) {
        if (err) {
            console.log('error rendering', err);
        } else {
            console.log('rendered');
        }
    }

    loadSampleBPMN() {
      this.viewer = new this.BpmnViewer({ container: '#canvas' });
        const url = 'assets/diagrams/pizza-collaboration.bpmn';
        this.http.get(url)
            .toPromise()
            .then(response => response.text())
            .then(data => this.viewer.importXML(data, this.handleError))
            .catch(this.handleError);
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
        //alert(JSON.stringify(response.formmodel).length);
        //if not empty
        let model=JSON.parse("{\"learningform\":\"empty\"}");
        if(JSON.stringify(response.formmodel).length !=2 ){
          model = JSON.parse(response.formmodel).learningform;
        }
        
        this.learningEngineService.getcurrentlpstatus(this.lpid).subscribe(response => {
          if (response.status == "success") {
            //alert(JSON.stringify(model));
            let prompt = JSON.parse("{\"steps\": [{ \"intro\": \"welcome to case \"},{ \"intro\": \"In this scenario you will open a case \"}]}");
            let component = this.createDynamicComponentwithModel(response.htmlform, model, prompt, this);
            //console.log(response);
            let componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
            this.parent.clear();
            let componentRef = this.parent.createComponent(componentFactory);

            componentRef.changeDetectorRef.detectChanges();

            this.loadSampleBPMN();
          } else if (response.status == "error") {
            //TODO: better error handling
            if (response.errortype == "unexpected") {

              /*Modal*/

              const activeModal = this.modalService.open(DefaultModal, {
                size: 'lg',

              });
              activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
              activeModal.componentInstance.modalContent = response.errorMsg;
              activeModal.result.then((result) => {
                //redirect to back hom
                this.router.navigate(['/pages', 'home', 'availablelearningpaths']);
              }, (reason) => {
                //do nothing
              });
              /*Modal*/
            } else if (response.errortype == "lpnonexistant") {
              /*Modal*/

              const activeModal = this.modalService.open(DefaultModal, {
                size: 'lg',

              });
              activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
              activeModal.componentInstance.modalContent = "Not a correct Learning path. Choose correct one. Taking you back!";
              activeModal.result.then((result) => {
                //redirect to back hom
                this.router.navigate(['/pages', 'home', 'availablelearningpaths']);
              }, (reason) => {
                //do nothing
              });
              /*Modal*/
            }

          }

        });
      } else {
        //TODO: Better error handling
        /*Modal*/

        const activeModal = this.modalService.open(DefaultModal, {
          size: 'lg',

        });
        activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
        activeModal.componentInstance.modalContent = response.errorMsg;
        activeModal.result.then((result) => {
          //redirect to back hom
          this.router.navigate(['/pages', 'home', 'availablelearningpaths']);
        }, (reason) => {
          //do nothing
        });
        /*Modal*/

      }

    });

  }

  completeLearning(learningForm: string) {

    //submit the form
    this.learningEngineService.completeLearningTask(this.lpid, "1", learningForm).subscribe(response => {
      //get the response after submitting the task
      console.log(response.status);
      if (response.status == "success") {
        console.log("Setting alter mesage");
        this.learningEngineService.publishAlertMsg("Completed task")
        this.loadForm();

      } else if (response.status == 'error') {
        /*Modal*/

        const activeModal = this.modalService.open(DefaultModal, {
          size: 'lg',

        });
        activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
        activeModal.componentInstance.modalContent = response.errorMsg;
        activeModal.result.then((result) => {
          //redirect to back hom

        }, (reason) => {
          //do nothing
        });
        /*Modal*/
      }
    });


  }

  startLearningScenario(lpinstid: string) {
    this.learningEngineService.startalearningscenario(this.lpid, lpinstid).subscribe(response => {
      if (response.status == "success") {
        this.loadForm();
      } else {
        //TODO: Better Error Handling
        /*Modal*/

        const activeModal = this.modalService.open(DefaultModal, {
          size: 'lg',

        });
        activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
        activeModal.componentInstance.modalContent = response.errorMsg;
        activeModal.result.then((result) => {
          //redirect to back hom
          this.router.navigate(['/pages', 'home', 'availablelearningpaths']);
        }, (reason) => {
          //do nothing
        });
        /*Modal*/
      }
    });
  }
}