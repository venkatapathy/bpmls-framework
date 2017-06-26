import { Routes, RouterModule }  from '@angular/router';

import { Home } from './home.component';
import { AvailableLearningPathsComponent } from './availablelearningpaths/availablelearningpaths.component';
import { DemoHome } from './demohome/demohome.component';
import { RunningLearningPathsComponent } from './runninglearningpaths/runninglearningpaths.component';
import { LearningSimulator } from './learningSimulator/learningSimulator.component';
import {AuthGuard} from '../../_guards/auth.guard';
import { SurveyHome } from './surveyhome/surveyhome.component';
// noinspection TypeScriptValidateTypes
const routes: Routes = [
      {path: 'demohome',component:DemoHome,canActivate: [AuthGuard] },
      {path: 'surveyhome',component:SurveyHome,canActivate: [AuthGuard] },
      { path: 'availablelearningpaths', component: AvailableLearningPathsComponent,canActivate: [AuthGuard] },
      { path: 'runninglearningpaths', component: RunningLearningPathsComponent,canActivate: [AuthGuard] },
      { path: 'learningsimulator/:id', component: LearningSimulator,canActivate: [AuthGuard] },
    
];

export const routing = RouterModule.forChild(routes);
