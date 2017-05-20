import { Component, AfterViewInit, NgZone } from '@angular/core';
import { TreeModel } from 'ng2-tree';
import { LearningEngineService } from '../learningengine.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModal } from '../../components/default-modal/default-modal.component';
import { Router } from '@angular/router';

@Component({
  selector: 'running-lps',
  templateUrl: './runninglearningpaths.html',
})

export class RunningLearningPathsComponent implements AfterViewInit {
  availableLPS: JSON;

  constructor(private modalService: NgbModal, private learningEngineService: LearningEngineService,
    private zone: NgZone, private router: Router) {
  }

  ngAfterViewInit() {
    this.loadAvailableLearningPaths();
  }

  loadAvailableLearningPaths() {
    this.learningEngineService.getrunninglearningpaths().subscribe(response => {
      this.zone.run(() => {
        if (response.status == 'error') {

          const activeModal = this.modalService.open(DefaultModal, {
            size: 'sm',
            backdrop: 'static',
          });
          activeModal.componentInstance.modalHeader = 'Static modal';
          activeModal.componentInstance.modalContent = response.status.error.errMsg;
          activeModal.result.then((result) => {
            // do nothing so return function
            return;
          }, (reason) => {
            // do nothing
            return;
          });
        }
        this.availableLPS = response;

      });

      // console.log(response);
    });
  }

  continueaLearningPath(lpid: string) {

    this.router.navigate(['/pages', 'home', 'learningsimulator', lpid]);



  }
}
