import { NgZone, OnInit, Inject, Type, Component, ViewChild, ViewContainerRef, AfterViewInit } from '@angular/core';
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
declare var flowchart:any;

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
  @ViewChild('bpmnDiagramContainer', { read: ViewContainerRef }) bpmnDiagramParent: ViewContainerRef;
  @ViewChild('pathFlowDiagram', { read: ViewContainerRef }) pathFlowDigram: ViewContainerRef;
  isChecked: boolean = false;
  BpmnNavigatedViewer: any;
  flowChart: any;
  private _window: Window;
  constructor(private http: Http, windowRef: WindowRefService, private router: Router, private modalService: NgbModal, private route: ActivatedRoute, private adHocComponentFactoryCreator: AdHocComponentFactoryCreator, private learningEngineService: LearningEngineService) {
    this._window = windowRef.nativeWindow;
    this.BpmnNavigatedViewer = (<any>this._window).BpmnJS;
    
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

  createDiagramComponent(BpmnNavigatedViewer: any, data: string, available: JSON[], running: JSON, completed: JSON[]) {
    
    @Component({
      template: '<div id="diagram" style="height: 100px;background-color:white"></div>'
    })
    class InsertedComponent implements OnInit, AfterViewInit {
      
      
      _window:any;
      
      constructor(windowRef: WindowRefService) {
        this._window = windowRef.nativeWindow;
        
        
      }
      ngOnInit() {


      }
      ngAfterViewInit() {
        //this.viewer = new BpmnViewer({ container: '#canvas' });
        var flowChart = (<any>this._window).flowchart;
        
        var diagram = flowChart.parse('st=>start: Start:>http://www.google.com[blank]\n' +
          'e=>end:>http://www.google.com\n' +
          'op1=>operation: My Operation |past\n' +
          'sub1=>subroutine: My Subroutine\n' +
          'cond=>condition: Yes \n' +
          'or No?\n:>http://www.google.com' +
          'io=>inputoutput|request: catch something...\n' +
          '' +
          'st(right)->op1');// the other symbols too...
        diagram.drawSVG('diagram', {
                      // 'x': 30,
                      // 'y': 50,
                      'line-width': 3,
                      'maxWidth': 3,//ensures the flowcharts fits within a certian width
                      'line-length': 80,
                      'text-margin': 10,
                      'font-size': 14,
                      'font': 'normal',
                      'font-family': 'Helvetica',
                      'font-weight': 'normal',
                      'font-color': 'black',
                      'line-color': 'black',
                      'element-color': 'black',
                      'fill': 'white',
                      'yes-text': 'yes',
                      'no-text': 'no',
                      'arrow-end': 'block',
                      'scale': 1,
                      'symbols': {
                        'start': {
                          'font-color': 'red',
                          'element-color': 'green',
                          'fill': 'yellow'
                        },
                        'end':{
                          'class': 'end-element'
                        }
                      },
                      'flowstate' : {
                        'past' : { 'fill' : '#CCCCCC', 'font-size' : 12},
                        'current' : {'fill' : 'yellow', 'font-color' : 'red', 'font-weight' : 'bold'},
                        'future' : { 'fill' : '#FFFF99'},
                        'request' : { 'fill' : 'blue'},
                        'invalid': {'fill' : '#444444'},
                        'approved' : { 'fill' : '#58C4A3', 'font-size' : 12, 'yes-text' : 'APPROVED', 'no-text' : 'n/a' },
                        'rejected' : { 'fill' : '#C45879', 'font-size' : 12, 'yes-text' : 'n/a', 'no-text' : 'REJECTED' }
                      }
                    });
      }
      loadSampleBPMN() {


      }

      handleError(err: any) {
        // this.ngZOne.run(() => {
        console.log(err);
        if (err) {
          console.log('error rendering', err);
        } else {
          console.log('rendered');

        }
        // });

      }
    }

    return InsertedComponent;
  }

  loadForm() {
    //try to get the model
    this.learningEngineService.getcurrenttaskmodel(this.lpid).subscribe(response => {
      if (response.status == "success") {
        //alert(JSON.stringify(response.formmodel).length);
        //if not empty
        let model = JSON.parse("{\"learningform\":\"empty\"}");
        if (JSON.stringify(response.formmodel).length != 2) {
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

            //get the pathflowdiagram and if successful display it
            this.learningEngineService.getpathflow(this.lpid).subscribe(response => {
              //failure we gonna sink quitely
              if (response.status == "success") {

                let component = this.createDiagramComponent(this.BpmnNavigatedViewer, response.data, response.available, response.running, response.completed);
                let componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
                this.bpmnDiagramParent.clear();
                let componentRef = this.pathFlowDigram.createComponent(componentFactory);

                componentRef.changeDetectorRef.detectChanges();
              }
            });
            /*const url = 'assets/diagrams/pizza-collaboration.bpmn';
            this.http.get(url)
              .toPromise()
              .then(response => response.text())
              .then(data => {
                let component = this.createDiagramComponent(this.BpmnNavigatedViewer, data);
                let componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
                this.bpmnDiagramParent.clear();
                let componentRef = this.bpmnDiagramParent.createComponent(componentFactory);

                componentRef.changeDetectorRef.detectChanges();

              });*/

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