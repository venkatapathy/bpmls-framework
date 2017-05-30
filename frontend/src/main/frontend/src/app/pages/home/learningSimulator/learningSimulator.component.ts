import { NgZone, OnInit, Inject, Type, Component, ViewChild, ViewContainerRef, AfterViewInit } from '@angular/core';
import { LearningEngineService } from '../learningengine.service';
import { ActivatedRoute } from '@angular/router';
import { NgForm } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModal } from '../../components/default-modal/default-modal.component';
import { Router } from '@angular/router';
import { WindowRefService } from '../window.ref.service';
import { Http } from '@angular/http';
import { DynamicComponentService } from '../dynamiccomponent.service';


//for demo tour
import { DemoStages } from './demofiles/demostages';
import { freshSimulator } from './demofiles/freshSimulator';
import { simulatorIntroduction } from './demofiles/simulatorIntro';

declare var introJs: any;


@Component({
  selector: 'learning-simulator',
  templateUrl: './learningSimulator.html',
})
export class LearningSimulator implements AfterViewInit {

  private lpid: string;
  public lpname: string;
  //for demo purpose
  private currentDemoStage: any;
  isDemo: boolean = false;
  private demoComplete: boolean = false;
  @ViewChild('taskFormContainer', { read: ViewContainerRef }) formContainer: ViewContainerRef;
  @ViewChild('bpmnDiagramContainer', { read: ViewContainerRef }) bpmnDiagramContainer: ViewContainerRef;
  @ViewChild('pathFlowDiagram', { read: ViewContainerRef }) pathFlowContainer: ViewContainerRef;



  helpHidden: boolean = false;

  private _window: Window;
  constructor(private router: Router, private modalService: NgbModal, private route: ActivatedRoute,
    private learningEngineService: LearningEngineService, private dynamicComponentService: DynamicComponentService) {
    this.learningEngineService.alertMsg$.subscribe((demoFlag) => {
      this.isDemo = demoFlag;
    })


  }

  ngAfterViewInit() {
    this.route
      .params
      .subscribe(params => {
        // Defaults to 0 if no query param provided.
        this.lpid = params['id'] || '0';
        // console.log("initalized lpinstid: " + this.lpid)
      });


    //before loading, if its a demo set the demo flag
    if (this.lpid == 'demoprocess') {
      this.learningEngineService.alertMsg.next(true);
      this.demoComplete = true;
    } else {
      this.learningEngineService.alertMsg.next(false);
    }

    this.loadForm();


    /*const prompt = JSON.parse('{\"steps\": [{ \"element\": \"#input01\",\"intro\": \"welcome to case \"},' +
              '{ \"intro\": \"In this scenario you will open a case \"}],\"disableInteraction\":false}');

    intro.setOptions(prompt);
    


    intro.start();*/
  }


private static ReviveDateTime(key: any, value: any): any {
        
       /* if (typeof value === 'string') {
          
            let a = new Date(value);
            
            if (!isNaN( a.getTime() )) {
                //alert(a);
                return a;

            }
        }*/

        return value;
    }

  loadForm() {



    // try to get the model
    this.learningEngineService.getcurrenttaskmodel(this.lpid).subscribe(response => {
      if (response.status == 'success') {
        // alert(JSON.stringify(response.formmodel).length);
        // if not empty
        let model = JSON.parse('{\"learningform\":\"empty\"}');
        if (JSON.stringify(response.formmodel).length != 2) {
          model = JSON.parse(JSON.stringify(response.formmodel.learningform),LearningSimulator.ReviveDateTime);
        }

        this.learningEngineService.getcurrentlpstatus(this.lpid).subscribe(lpresponse => {
          if (lpresponse.status == 'success') {
            // for demo purpose

            const tempStage: string = lpresponse.demostage;
            // console.log(tempStage);
            this.currentDemoStage = DemoStages[tempStage];


            // get the prompt



            this.dynamicComponentService.createDynamicFormComponentwithModel(this.formContainer,
              lpresponse.htmlform, model, this);


            // get the pathflowdiagram and if successful display it
            this.learningEngineService.getpathflow(this.lpid).subscribe(pfresponse => {
              // failure we gonna sink quitely
              if (pfresponse.status == 'success') {
                this.lpname = pfresponse.lpname;
                this.dynamicComponentService.createFlowDiagramComponent(this.pathFlowContainer, pfresponse.flowdata);
              }
            });

            // get the processdiagramdetails and if successful display it
            this.learningEngineService.getprocessdigramdetails(this.lpid).subscribe(pfresponse => {
              // failure we gonna sink quitely
              if (pfresponse.status == 'success') {

                this.dynamicComponentService.createProcessDiagramComponent(this.bpmnDiagramContainer,
                  pfresponse.xmldata, pfresponse.available, pfresponse.running, pfresponse.completed, pfresponse.trace);
              }else{
                this.bpmnDiagramContainer.clear();
              }
            });

            //for demo purpose
            if (this.isDemo) {
              //call demoTour
              this.demotour();
            }

          } else if (response.status == 'error') {

            if (response.errortype == 'unexpected') {

              /*Modal*/

              const activeModal = this.modalService.open(DefaultModal, {
                size: 'lg',

              });
              activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
              activeModal.componentInstance.modalContent = response.errorMsg;
              activeModal.result.then((result) => {
                // redirect to back hom
                this.router.navigate(['/pages', 'home', 'availablelearningpaths']);
              }, (reason) => {
                // do nothing
              });
              /*Modal*/
            } else if (response.errortype == 'lpnonexistant') {
              /*Modal*/

              const activeModal = this.modalService.open(DefaultModal, {
                size: 'lg',

              });
              activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
              activeModal.componentInstance.modalContent =
                'Not a correct Learning path. Choose correct one. Taking you back!';
              activeModal.result.then((result) => {
                // redirect to back hom
                this.router.navigate(['/pages', 'home', 'availablelearningpaths']);
              }, (reason) => {
                // do nothing
              });
              /*Modal*/
            }

          }

        });
      } else {

        /*Modal*/

        const activeModal = this.modalService.open(DefaultModal, {
          size: 'lg',

        });
        activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
        activeModal.componentInstance.modalContent = response.errorMsg;
        activeModal.result.then((result) => {
          // redirect to back hom
          this.router.navigate(['/pages', 'home', 'availablelearningpaths']);
        }, (reason) => {
          // do nothing
        });
        /*Modal*/

      }

    });
    //for demo purposes

    //if (this.learningEngineService.freshDemo) {


    //}


  }

  completeLearning(learningForm: string) {

    // submit the form
    this.learningEngineService.completeLearningTask(this.lpid, "1", learningForm).subscribe(response => {
      // get the response after submitting the task
      // console.log(response.status);

      if (response.status == 'success') {
        // console.log("'Setting alter mesage'");

        this.loadForm();

      } else if (response.status == 'error') {
        /*Modal*/

        const activeModal = this.modalService.open(DefaultModal, {
          size: 'lg',

        });
        activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
        activeModal.componentInstance.modalContent = response.errorMsg;
        activeModal.result.then((result) => {
          // redirect to back hom

        }, (reason) => {
          // do nothing
        });
        /*Modal*/
      }
    });


  }

  startLearningScenario(lpinstid: string) {
    this.learningEngineService.startalearningscenario(this.lpid, lpinstid).subscribe(response => {
      if (response.status == 'success') {
        this.loadForm();
      } else {
        // TODO: Better Error Handling
        /*Modal*/

        const activeModal = this.modalService.open(DefaultModal, {
          size: 'lg',

        });
        activeModal.componentInstance.modalHeader = 'Error Acessing Learning Simulator';
        activeModal.componentInstance.modalContent = response.errorMsg;
        activeModal.result.then((result) => {
          // redirect to back hom
          this.router.navigate(['/pages', 'home', 'availablelearningpaths']);
        }, (reason) => {
          // do nothing
        });
        /*Modal*/
      }
    });
  }

  //for demo purposes
  demotour() {

    //only when the prev demo is complete or its a fresh demo in which case the demoComplete flag is set to true
    if (this.demoComplete) {
      //console.log(this.currentDemoStage);
      this.demoComplete = false;

      if (this.currentDemoStage == DemoStages.freshSimulator) {
        var intro = introJs();
        const prompt = freshSimulator;
        intro.setOptions(prompt);

        intro.oncomplete(() => {
          this.demoComplete = true;
        });

        intro.onexit(() => {
          this.demotour();
        });
        intro.start();

        //next stage
        this.currentDemoStage = DemoStages.simulatorIntroduction;
      }

      if (this.currentDemoStage == DemoStages.simulatorIntroduction) {
        var intro = introJs();
        const prompt = simulatorIntroduction;
        intro.setOptions(prompt);
        intro.oncomplete(function () {
          //console.log("hi");
          this.demoComplete = true;

        });
        intro.start();


      }
    }
  }



  toggleHelp() {
    if (this.helpHidden) {
      this.helpHidden = false;
    } else {
      this.helpHidden = true;
    }

  }
}