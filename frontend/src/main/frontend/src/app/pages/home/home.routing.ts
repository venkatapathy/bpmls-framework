import { Routes, RouterModule }  from '@angular/router';

import { Home } from './home.component';
import { TreeView } from './treeView/treeView.component';
import { LearningSimulator } from './learningSimulator/learningSimulator.component';
// noinspection TypeScriptValidateTypes
const routes: Routes = [
  {
    path: '',
    component: Home,
    children: [
      { path: 'treeview', component: TreeView },
      { path: 'learningsimulator/:id', component: LearningSimulator },
    ],
  },
];

export const routing = RouterModule.forChild(routes);
