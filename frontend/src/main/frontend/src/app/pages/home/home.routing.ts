import { Routes, RouterModule }  from '@angular/router';

import { Home } from './home.component';
import { AvailableLearningPathsComponent } from './availablelearningpaths/availablelearningpaths.component';
import { RunningLearningPathsComponent } from './runninglearningpaths/runninglearningpaths.component';
import { LearningSimulator } from './learningSimulator/learningSimulator.component';
// noinspection TypeScriptValidateTypes
const routes: Routes = [
  
      { path: 'availablelearningpaths', component: AvailableLearningPathsComponent },
      { path: 'runninglearningpaths', component: RunningLearningPathsComponent },
      { path: 'learningsimulator/:id', component: LearningSimulator },
    
];

export const routing = RouterModule.forChild(routes);
