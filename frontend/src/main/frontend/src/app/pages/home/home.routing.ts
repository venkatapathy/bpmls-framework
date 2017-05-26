import { Routes, RouterModule }  from '@angular/router';

import { Home } from './home.component';
import { AvailableLearningPathsComponent } from './availablelearningpaths/availablelearningpaths.component';
import { RunningLearningPathsComponent } from './runninglearningpaths/runninglearningpaths.component';
import { LearningSimulator } from './learningSimulator/learningSimulator.component';
import {AuthGuard} from '../../_guards/auth.guard';

// noinspection TypeScriptValidateTypes
const routes: Routes = [
  
      { path: 'availablelearningpaths', component: AvailableLearningPathsComponent,canActivate: [AuthGuard] },
      { path: 'runninglearningpaths', component: RunningLearningPathsComponent,canActivate: [AuthGuard] },
      { path: 'learningsimulator/:id', component: LearningSimulator,canActivate: [AuthGuard] },
    
];

export const routing = RouterModule.forChild(routes);
