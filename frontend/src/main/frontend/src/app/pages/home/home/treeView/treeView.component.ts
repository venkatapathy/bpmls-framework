import { Component, AfterViewInit, NgZone } from '@angular/core';
import { TreeModel } from 'ng2-tree';
import { LearningEngineService } from '../../learningengine.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModal } from '../../../ui/components/modals/default-modal/default-modal.component';


@Component({
  selector: 'tree-view',
  templateUrl: './treeView.html',
})

export class TreeView implements AfterViewInit {
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
        ]
      }
    ]
  };

  constructor(private modalService: NgbModal,private learningEngineService: LearningEngineService, private zone: NgZone) {
  }

  ngAfterViewInit() {
    this.loadAvailableLearningPaths();
  }

  loadAvailableLearningPaths() {
    this.learningEngineService.getavailablelearningpaths().subscribe(response => {
      this.zone.run(() => {
        this.availableLPS = response;

      });

      console.log(response);
    });
  }

  startaLearningPath(lpid: string) {
    this.learningEngineService.startaLearningPath(lpid).subscribe(response => {
      if (response.error) {
        const activeModal = this.modalService.open(DefaultModal, {
          size: 'sm',
          backdrop: 'static'
        });
        activeModal.componentInstance.modalHeader = 'Static modal';
        activeModal.componentInstance.modalContent = `This is static modal, backdrop click
                                                    will not close it. Click × or confirmation button to close modal.`;
      }
      if(response.success){
        const activeModal = this.modalService.open(DefaultModal, {
          size: 'sm',
          backdrop: 'static'
        });
        activeModal.componentInstance.modalHeader = 'Starting a Learning Path';
        activeModal.componentInstance.modalContent = `Successfully started a learning path. 
                                                        You will be taken to Simulator`;
        activeModal.componentInstance.nextpageURL= "home/learningsimulator";
         activeModal.componentInstance.lpinstid= response.success.lpinstid;


      }
    });
  }
}
