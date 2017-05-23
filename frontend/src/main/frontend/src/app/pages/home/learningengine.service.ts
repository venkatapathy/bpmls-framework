import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'
import { Subject } from 'rxjs/Subject';
import {hostaddress} from './hostaddress';

@Injectable()
export class LearningEngineService {
    public currentTaskHtml: any;
    public alertMessage: string;
    private alertMsg = new Subject<string>();
    alertMsg$ = this.alertMsg.asObservable();

    constructor(private http: Http) { }

    getavailablelearningpaths() {
        //console.log('http://localhost:8080/getavailablelearningpaths');

        return this.http.get(hostaddress.host+'/getavailablelearningpaths')

            .map((response: Response) => {


                return response.json();
            });
    }

    getrunninglearningpaths() {
        //console.log('http://localhost:8080/getavailablelearningpaths');

        return this.http.get(hostaddress.host+'/getrunningpaths')

            .map((response: Response) => {


                return response.json();
            });
    }

    publishAlertMsg(textToPublish: string) {
        this.alertMsg.next(textToPublish);
    }

    getcurrenttaskmodel(lpintid: string) {
        //console.log('http://localhost:8080/getcurrentlearningtask');
        let params = new URLSearchParams();
        params.set('lpinstid', lpintid);
        return this.http.get(hostaddress.host+'/getcurrentlearningtaskmodel/' + lpintid, { search: params })

            .map((response: Response) => {


                return response.json();
            });
    }


    getcurrentlpstatus(lpintid: string) {
        //console.log('http://localhost:8080/getcurrentlearningpathstatus/'+lpintid);
        let params = new URLSearchParams();
        params.set('lpinstid', lpintid);
        return this.http.get(hostaddress.host+'/getcurrentlearningpathstatus/' + lpintid, { search: params })

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return response.json();
            });
    }

    completeLearningTask(lpid: string, lsintid: string, learningForm: string) {
        let responseJson = "{\"lsinstid\":" + lsintid + ",\"learningform\":" + learningForm + "}";


        let headers = new Headers({ 'Content-Type': 'application/json' }); // ... Set content type to JSON


        return this.http.post(hostaddress.host+'/completelearningtask/' + lpid, responseJson) // ...using post request
            .map((res: Response) => { console.log(res); return res.json() }); // ...and calling .json() on the response to return data
        //...errors if any

    }

    startaLearningPath(lpid: string) {
        let responseJson = "{\"lpid\":\"" + lpid + "\"}";


        let headers = new Headers({ 'Content-Type': 'application/json' });
        let params = new URLSearchParams();

        console.log("Starting a learning path for: " + lpid);

        return this.http.post(hostaddress.host+'/startalearningpath/' + lpid, responseJson)

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return response.json();
            });
    }

    startalearningscenario(lpid: string, lpinstd: string) {
        let responseJson = "{\"lpid\":\"" + lpid + "\"}";


        let headers = new Headers({ 'Content-Type': 'application/json' });
        let params = new URLSearchParams();

        console.log("Starting a learning path for: " + lpid);

        return this.http.post(hostaddress.host+'/startalearningscenario/' + lpid + "/" + lpinstd, responseJson)

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return response.json();
            });
    }

    getpathflow(lpid: string) {
        return this.http.get(hostaddress.host+'/getlearningflowdiagram/'+lpid)

            .map((response: Response) => {


                return response.json();
            });
    }

    getprocessdigramdetails(lpid: string) {
        return this.http.get(hostaddress.host+'/getprocessdiagramdetails/'+lpid)

            .map((response: Response) => {


                return response.json();
            });
    }

}