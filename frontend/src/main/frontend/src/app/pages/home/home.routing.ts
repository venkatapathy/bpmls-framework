import { Routes, RouterModule }  from '@angular/router';

import { Home } from './home.component';
import { TreeView } from './home/treeView/treeView.component';
import { LearningSimulator } from './home/learningSimulator/learningSimulator.component';
// noinspection TypeScriptValidateTypes
const routes: Routes = [
  {
    path: '',
    component: Home,
    children: [
      { path: 'treeview', component: TreeView },
      { path: 'learningsimulator', component: LearningSimulator },
    ],
  },
];

export const routing = RouterModule.forChild(routes);