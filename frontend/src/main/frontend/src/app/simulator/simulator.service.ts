import { Injectable } from '@angular/core';
import { Http,Response,URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'

@Injectable()
export class SimulatorService {
    public currentTaskHtml:any;
    constructor(private http: Http) { }

    getcurrentlearningtask(lsintid: string, pinstid: string) {
        console.log('http://localhost:8080/getcurrentlearningtask');
        let params = new URLSearchParams();
        params.set('pinstid',pinstid);
        return this.http.get('http://localhost:8080/getcurrentlearningtask',{ search: params })

            .map((response: Response) => {
                // login successful if there's a jwt token in the response
               return response.text();
            });
    }

    logout() {
        // remove user from local storage to log user out
        localStorage.removeItem('currentUser');
    }
}