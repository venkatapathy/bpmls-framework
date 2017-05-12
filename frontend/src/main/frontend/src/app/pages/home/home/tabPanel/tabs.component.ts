import { Component, ContentChildren, QueryList, AfterContentInit } from '@angular/core';
import { Tab } from './tab.component';

// bootstrap tab structure for the template instead of using ng2 buttons
// <ul class="nav nav-tabs ">
//       <li *ngFor="let tab of tabs; let i = index" (click)="selectTab(tab)" [class.active]="tab.active" class="tab-content">
//         <a href="javascript:void(0)" >{{tab.title}}</a>
//       </li>
//     </ul>
//  <ng-content></ng-content>
    
@Component({
  selector: 'tabs',
 templateUrl: './tabs.html',
})
export class Tabs implements AfterContentInit {
  
  @ContentChildren(Tab) tabs: QueryList<Tab>;
  
  // contentChildren are set
  ngAfterContentInit() {
    // get all active tabs
    let activeTabs = this.tabs.filter((tab)=>tab.active);
    
    // if there is no active tab set, activate the first
    if(activeTabs.length === 0) {
      this.selectTab(this.tabs.first);
    }
  }
  
  selectTab(tab: Tab){
    // deactivate all tabs
    this.tabs.toArray().forEach(tab => tab.active = false);
    
    // activate the tab the user has clicked on.
    tab.active = true;
  }

}
