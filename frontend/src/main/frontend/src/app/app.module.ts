import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import {SimulatorComponent} from './simulator/simulator.component'
import {SimulatorService} from './simulator/simulator.service'
import {AdHocComponentFactoryCreator} from './simulator/adhoc-component-factory.service'
import {AlertsNMsgsComponent} from './alertsnmsgs/alertsnmsgs.component'
@NgModule({
  declarations: [
    AppComponent, SimulatorComponent,AlertsNMsgsComponent
  ],
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule
    
  ],
  
  providers: [SimulatorService,AdHocComponentFactoryCreator],
  bootstrap: [AppComponent]
})

export class AppModule { }
