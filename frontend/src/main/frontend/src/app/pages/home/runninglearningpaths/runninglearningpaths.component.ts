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
          activeModal.componentInstance.modalHeader = 'Error';
          activeModal.componentInstance.modalContent = "User not authenticated";
          activeModal.result.then((result) => {
           this.router.navigate(['/login']);
            
          }, (reason) => {
            this.router.navigate(['/login']);
            
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
