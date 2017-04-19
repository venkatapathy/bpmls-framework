import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[form-host]',
})
export class FormDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}



/*
Copyright 2017 Google Inc. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at http://angular.io/license
*/