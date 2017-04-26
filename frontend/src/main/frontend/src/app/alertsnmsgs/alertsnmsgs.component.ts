import { Component,Input, ChangeDetectorRef } from '@angular/core';
import {SimulatorService} from '../simulator/simulator.service'
@Component({
  selector: 'alertsnmsgs',
  templateUrl: './alertsnmsgs.component.html',
  
})
export class AlertsNMsgsComponent {
  message = 'app works!';

  constructor(private simulatorService: SimulatorService){ }
  public setMessage(message){
      this.simulatorService.alertMsg$.subscribe(data=>this.message=data);
      
  }
}
