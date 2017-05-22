import { NgZone, OnInit, Inject, Type, Component, ViewChild, ViewContainerRef, AfterViewInit } from '@angular/core';
import { LearningEngineService } from '../learningengine.service';
import { ActivatedRoute } from '@angular/router';
import { NgForm } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModal } from '../../components/default-modal/default-modal.component';
import { Router } from '@angular/router';
import { WindowRefService } from '../window.ref.service';
import { Http } from '@angular/http';
import { DynamicComponentService } from '../dynamiccomponent.service'


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
  @ViewChild('taskFormContainer', { read: ViewContainerRef }) formContainer: ViewContainerRef;
  @ViewChild('bpmnDiagramContainer', { read: ViewContainerRef }) bpmnDiagramContainer: ViewContainerRef;
  @ViewChild('pathFlowDiagram', { read: ViewContainerRef }) pathFlowContainer: ViewContainerRef;

  isChecked: boolean = false;
 

  private _window: Window;
  constructor(private router: Router, private modalService: NgbModal, private route: ActivatedRoute,
    private learningEngineService: LearningEngineService, private dynamicComponentService: DynamicComponentService) {

    

  }

  ngAfterViewInit() {
    this.route
      .params
      .subscribe(params => {
        // Defaults to 0 if no query param provided.
        this.lpid = params['id'] || '0';
        // console.log("initalized lpinstid: " + this.lpid)
      });


    this.loadForm();

  }



  loadForm() {
    // try to get the model
    this.learningEngineService.getcurrenttaskmodel(this.lpid).subscribe(response => {
      if (response.status == 'success') {
        // alert(JSON.stringify(response.formmodel).length);
        // if not empty
        let model = JSON.parse('{\"learningform\":\"empty\"}');
        if (JSON.stringify(response.formmodel).length != 2) {
          model = JSON.parse(response.formmodel).learningform;
        }

        this.learningEngineService.getcurrentlpstatus(this.lpid).subscribe(lpresponse => {
          if (lpresponse.status == 'success') {
            // get the prompt
            const prompt = JSON.parse('{\"steps\": [{ \"intro\": \"welcome to case \"},' +
              '{ \"intro\": \"In this scenario you will open a case \"}]}');


            this.dynamicComponentService.createDynamicFormComponentwithModel(this.formContainer,
              lpresponse.htmlform, model, prompt, this);


            // get the pathflowdiagram and if successful display it
            this.learningEngineService.getpathflow(this.lpid).subscribe(pfresponse => {
              // failure we gonna sink quitely
              if (pfresponse.status == 'success') {

                this.dynamicComponentService.createFlowDiagramComponent(this.pathFlowContainer, pfresponse.flowdata);
              }
            });

            // get the processdiagramdetails and if successful display it
            this.learningEngineService.getprocessdigramdetails(this.lpid).subscribe(pfresponse => {
              // failure we gonna sink quitely
              if (pfresponse.status == 'success') {

                this.dynamicComponentService.createProcessDiagramComponent(this.bpmnDiagramContainer,
                  pfresponse.xmldata, pfresponse.available, pfresponse.running, pfresponse.completed,pfresponse.trace);
              }
            });

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

  }

  completeLearning(learningForm: string) {

    // submit the form
    this.learningEngineService.completeLearningTask(this.lpid, "1", learningForm).subscribe(response => {
      // get the response after submitting the task
      console.log(response.status);
      if (response.status == 'success') {
        // console.log("'Setting alter mesage'");
        this.learningEngineService.publishAlertMsg('Completed task');
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
}