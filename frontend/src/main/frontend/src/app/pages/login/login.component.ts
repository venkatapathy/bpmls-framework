import {Component,AfterViewInit} from '@angular/core';
import {FormGroup, AbstractControl, FormBuilder, Validators} from '@angular/forms';
import { AppState } from '../../app.service';
import { Router } from '@angular/router';
import {EmailValidator} from '../../theme/validators';

@Component({
  selector: 'login',
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login implements AfterViewInit{

  public form:FormGroup;
  public email:AbstractControl;
  public password:AbstractControl;
  public submitted:boolean = false;

  constructor(fb:FormBuilder, private appservice:AppState,private router: Router) {
    this.form = fb.group({
      'email': ['', Validators.compose([Validators.required, EmailValidator.validate])],
      'password': ['', Validators.compose([Validators.required, Validators.minLength(4)])]
    });

    this.email = this.form.controls['email'];
    this.password = this.form.controls['password'];
  }

  ngAfterViewInit() {
      localStorage.removeItem('currentUser');
  }
  public onSubmit(values:any):void {
    this.submitted = true;
    if (this.form.valid) {
      // your code goes here
       // console.log(values);
       this.appservice.authenticateuser(values.email,values.password).subscribe((response)=>{
        if(response.status == 'success'){
          alert("Login Successful, taking you to Home page");
          this.router.navigate(['/pages']);
        }else if(response.status== 'error'){
          alert(response.errMsg);
        }
      });
    }
  }
}
