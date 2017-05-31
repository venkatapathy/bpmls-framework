import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'
import { Subject } from 'rxjs/Subject';
import { hostaddress } from '../../../assets/js/hostaddress';

@Injectable()
export class LearningEngineService {
    public currentTaskHtml: any;

    public alertMsg = new Subject<boolean>();


    alertMsg$ = this.alertMsg.asObservable();

    constructor(private http: Http) {

    }

    getavailablelearningpaths() {
        //console.log('http://localhost:8080/getavailablelearningpaths');
        let responseJson = "{\"username\":" + localStorage.getItem('currentUser') + "}";

        return this.http.post(hostaddress.host + '/getavailablelearningpaths', responseJson)

            .map((response: Response) => {


                return response.json();
            });
    }

    getrunninglearningpaths() {
        //console.log('http://localhost:8080/getavailablelearningpaths');
        let responseJson = "{\"username\":" + localStorage.getItem('currentUser') + "}";
        return this.http.post(hostaddress.host + '/getrunningpaths', responseJson)

            .map((response: Response) => {


                return response.json();
            });
    }

    isDemo(setAsDemo: boolean) {
        this.alertMsg.next(setAsDemo);
    }

    
    getcurrenttaskmodel(lpintid: string) {
        //console.log('http://localhost:8080/getcurrentlearningtask');
        let responseJson = "{\"username\":" + localStorage.getItem('currentUser') + "}";

        let params = new URLSearchParams();
        params.set('lpinstid', lpintid);
        return this.http.post(hostaddress.host + '/getcurrentlearningtaskmodel/' + lpintid, responseJson)

            .map((response: Response) => {


                return response.json();
            });
    }


    getcurrentlpstatus(lpid: string) {
        //console.log('http://localhost:8080/getcurrentlearningpathstatus/'+lpintid);
        let responseJson = "{\"username\":" + localStorage.getItem('currentUser') + "}";


        let headers = new Headers({ 'Content-Type': 'application/json' });

        let params = new URLSearchParams();

        params.set('lpinstid', lpid);
        return this.http.post(hostaddress.host + '/getcurrentlearningpathstatus/' + lpid, responseJson)

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return response.json();
            });
    }

    completeLearningTask(lpid: string, lsintid: string, learningForm: string) {
        let responseJson = "{\"lsinstid\":" + lsintid + ",\"username\":" + localStorage.getItem('currentUser') + ",\"learningform\":" + learningForm + "}";


        let headers = new Headers({ 'Content-Type': 'application/json' }); // ... Set content type to JSON


        return this.http.post(hostaddress.host + '/completelearningtask/' + lpid, responseJson) // ...using post request
            .map((res: Response) => {
                // console.log(res); 
                return res.json()
            }); // ...and calling .json() on the response to return data
        //...errors if any

    }

    startaLearningPath(lpid: string) {
        let responseJson = "{\"username\":" + localStorage.getItem('currentUser') + "}";


        let headers = new Headers({ 'Content-Type': 'application/json' });
        let params = new URLSearchParams();

        // console.log("Starting a learning path for: " + lpid);

        return this.http.post(hostaddress.host + '/startalearningpath/' + lpid, responseJson)

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return response.json();
            });
    }

    startalearningscenario(lpid: string, lpinstd: string) {
        let responseJson = "{\"username\":" + localStorage.getItem('currentUser') + "}";


        let headers = new Headers({ 'Content-Type': 'application/json' });
        let params = new URLSearchParams();

        // console.log("Starting a learning path for: " + lpid);

        return this.http.post(hostaddress.host + '/startalearningscenario/' + lpid + "/" + lpinstd, responseJson)

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return response.json();
            });
    }

    getpathflow(lpid: string) {
        let responseJson = "{\"username\":" + localStorage.getItem('currentUser') + "}";


        let headers = new Headers({ 'Content-Type': 'application/json' });
        let params = new URLSearchParams();

        return this.http.post(hostaddress.host + '/getlearningflowdiagram/' + lpid, responseJson)

            .map((response: Response) => {


                return response.json();
            });
    }

    getprocessdigramdetails(lpid: string) {
        let responseJson = "{\"username\":" + localStorage.getItem('currentUser') + "}";


        let headers = new Headers({ 'Content-Type': 'application/json' });
        let params = new URLSearchParams();

        return this.http.post(hostaddress.host + '/getprocessdiagramdetails/' + lpid, responseJson)

            .map((response: Response) => {


                return response.json();
            });
    }

    getoraclevalues(lpid: string) {
        let responseJson = "{\"username\":" + localStorage.getItem('currentUser') + "}";


        let headers = new Headers({ 'Content-Type': 'application/json' });
        let params = new URLSearchParams();

        return this.http.post(hostaddress.host + '/getoraclevalues/' + lpid, responseJson)

            .map((response: Response) => {


                return response.json();
            });
    }

}