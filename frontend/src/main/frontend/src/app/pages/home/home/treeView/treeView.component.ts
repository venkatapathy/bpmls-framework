import {Component} from '@angular/core';
import {TreeModel} from 'ng2-tree';

@Component({
  selector: 'tree-view',
  templateUrl: './treeView.html',
})

export class TreeView {

  tree: TreeModel = {
    value: 'The Learning Scenarios are:',
    children: [
      {
        value: 'Object-oriented programming'
      },
      {
        value: 'Prototype-based programming',
        children: [
          {value: 'JavaScript'},
          {value: 'CoffeeScript'},
          {value: 'Lua'},
        ]
      }
    ]
  };

  constructor() {
  }

}
