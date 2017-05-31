import { Injectable } from '@angular/core';
import { Http, Headers, Response,RequestOptions } from '@angular/http';
import { hostaddress } from '../assets/js/hostaddress';

export type InternalStateType = {
  [key: string]: any
};

@Injectable()
export class AppState {
  _state: InternalStateType = {};

  constructor(private http: Http) {
  }

  // already return a clone of the current state
  get state() {
    return this._state = this._clone(this._state);
  }

  // never allow mutation
  set state(value) {
    throw new Error('do not mutate the `.state` directly');
  }


  get(prop?: any) {
    // use our state getter for the clone
    const state = this.state;
    return state.hasOwnProperty(prop) ? state[prop] : state;
  }

  set(prop: string, value: any) {
    // internally mutate our state
    return this._state[prop] = value;
  }


  private _clone(object: InternalStateType) {
    // simple object clone
    return JSON.parse(JSON.stringify(object));
  }

  login(username: string, password: string) {
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    // console.log('http://localhost:8080/login', JSON.stringify({ username: username, password: password }), options);

    return this.http.post(hostaddress.host + '/login', JSON.stringify({ username: username, password: password }), options)

      .map((response: Response) => {
        // login successful if there's a jwt token in the response
        // console.log("mapping response");
        let user = response.json();
        if (user) {
          // store user details and jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('currentUser', JSON.stringify(user));
        }
      });
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
  }

  registeruser(username: string, password: string){
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    //console.log('http://localhost:8080/login', JSON.stringify({ username: username, password: password }), options);

    return this.http.post(hostaddress.host + '/registernewuser', JSON.stringify({ username: username, password: password }), options)

      .map((response: Response) => {
        // login successful if there's a jwt token in the response
        
        
        return response.json();
      });
  }

  authenticateuser(username: string, password: string){
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    //console.log('http://localhost:8080/login', JSON.stringify({ username: username, password: password }), options);

    return this.http.post(hostaddress.host + '/authenticateuser', JSON.stringify({ username: username, password: password }), options)

      .map((response: Response) => {
        // login successful if there's a jwt token in the response
        
        let user = response.json();
        if (user.status == 'success') {
          // store user details and jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('currentUser', JSON.stringify(user.username));
        }
        return response.json();
      });
  }
}
