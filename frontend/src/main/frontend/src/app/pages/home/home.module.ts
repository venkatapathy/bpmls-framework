import { Home } from './home.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { NgaModule } from '../../theme/nga.module';
import { TreeModule } from 'ng2-tree';

import { routing } from './home.routing';

import { Tab } from './tabPanel/tab.component';
import { Tabs } from './tabPanel/tabs.component';
import { AvailableLearningPathsComponent } from './availablelearningpaths/availablelearningpaths.component';
import { RunningLearningPathsComponent } from './runninglearningpaths/runninglearningpaths.component';
import { LearningSimulator } from './learningSimulator/learningSimulator.component';
import { LsCompletionChart } from './learningSimulator/lsCompletionChart';
import { LsCompletionChartService } from './learningSimulator/lsCompletionChart/lsCompletionChart.service';
import { AdHocComponentFactoryCreator } from './adhoc-component-factory.service';
import { LearningEngineService } from './learningengine.service';
import { DefaultModal } from '../components/default-modal/default-modal.component';
import { WindowRefService } from './window.ref.service';
import { DynamicComponentService } from './dynamiccomponent.service';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgaModule,
    TreeModule,
    routing,
    NgbModalModule,
  ],
  declarations: [
    Home,
    AvailableLearningPathsComponent,
    RunningLearningPathsComponent,
    LearningSimulator,
    LsCompletionChart,
    Tabs,
    Tab,
    DefaultModal,
  ],
 entryComponents: [
    DefaultModal
  ],
  providers: [
    LsCompletionChartService,
    AdHocComponentFactoryCreator,
    LearningEngineService,
    WindowRefService,
    DynamicComponentService
  ]
})
export class HomeModule { }
