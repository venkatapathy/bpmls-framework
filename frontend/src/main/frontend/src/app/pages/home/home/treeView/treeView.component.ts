import { Component,AfterViewInit, NgZone } from '@angular/core';
import { TreeModel } from 'ng2-tree';
import { LearningEngineService } from './learningengine.service';

@Component({
  selector: 'tree-view',
  templateUrl: './treeView.html',
})

export class TreeView implements AfterViewInit{
   availableLPS:JSON;
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

  constructor(private learningEngineService: LearningEngineService,private zone:NgZone) {
  }

  ngAfterViewInit() {
    this.loadAvailableLearningPaths();
  }
  loadAvailableLearningPaths() {
    this.learningEngineService.getavailablelearningpaths().subscribe(response => {
      this.zone.run(()=>{
        this.availableLPS=response;
      
      });
      
      console.log(response);
    });
  }
}
