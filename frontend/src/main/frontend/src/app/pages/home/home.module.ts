import { Home } from './home.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


import { NgaModule } from '../../theme/nga.module';
import { TreeModule } from 'ng2-tree';

import { routing } from './home.routing';
import { Tab } from './home/tabPanel/tab.component';
import { Tabs } from './home/tabPanel/tabs.component';

import { TreeView } from './home/treeView/treeView.component';
import { LearningSimulator } from './home/learningSimulator/learningSimulator.component';

import { LsCompletionChart } from './home/learningSimulator/lsCompletionChart';
import { LsCompletionChartService } from './home/learningSimulator/lsCompletionChart/lsCompletionChart.service';

import {SimulatorService} from './home/learningSimulator/simulator.service';
import {AdHocComponentFactoryCreator} from './home/learningSimulator/adhoc-component-factory.service';

import {LearningEngineService} from './home/treeView/learningengine.service';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgaModule,
    TreeModule,
    routing,
  ],
  declarations: [
    Home,
    TreeView,
    LearningSimulator,
    LsCompletionChart,
    Tabs,
    Tab
  ],
  providers: [
    LsCompletionChartService,
    SimulatorService,
    AdHocComponentFactoryCreator,
    LearningEngineService
  ]
})
export class HomeModule {}
