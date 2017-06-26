import { Component, AfterViewInit, NgZone } from '@angular/core';
import { TreeModel } from 'ng2-tree';
import { LearningEngineService } from '../learningengine.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModal } from '../../components/default-modal/default-modal.component';
import { Router } from '@angular/router';

@Component({
  selector: 'available-lps',
  templateUrl: './availablelearningpaths.html',
})

export class AvailableLearningPathsComponent implements AfterViewInit {
  availableLPS: JSON;
  tree: TreeModel = {
    value: 'The Learning Scenarios are:',
    children: [
      {
        value: 'Object-oriented programming'
      },
      {
        value: 'Prototype-based programming',
        children: [
          { value: 'JavaScript' },
          { value: 'CoffeeScript' },
          { value: 'Lua' },
        ],
      },
    ],
  };

  constructor(private modalService: NgbModal, private learningEngineService: LearningEngineService,
    private zone: NgZone, private router: Router) {
  }

  ngAfterViewInit() {
    //alert(localStorage.getItem('currentUser'));
    this.loadAvailableLearningPaths();
  }

  loadAvailableLearningPaths() {
    this.learningEngineService.getavailablelearningpaths().subscribe(response => {
      this.zone.run(() => {
        if (response.status == 'error') {
         


            const activeModal = this.modalService.open(DefaultModal, {
              size: 'sm',
              backdrop: 'static',
            });
            activeModal.componentInstance.modalHeader = 'Error';
            activeModal.componentInstance.modalContent = response.errMsg;
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

  startaLearningPath(lpid: string) {
    this.learningEngineService.startaLearningPath(lpid).subscribe(response => {


      if (response.status == 'error') {

        const activeModal = this.modalService.open(DefaultModal, {
          size: 'sm',
          backdrop: 'static',
        });
        activeModal.componentInstance.modalHeader = 'Static modal';
        activeModal.componentInstance.modalContent = response.errMsg;
        activeModal.result.then((result) => {
          // do nothing
        }, (reason) => {
          // do nothing
        });
      }
      if (response.status == 'success') {



        this.router.navigate(['/pages', 'home', 'learningsimulator', response.lpid]);


      }
    });
  }
}
