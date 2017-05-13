import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'
import { Subject } from 'rxjs/Subject';

@Injectable()
export class SimulatorService {
    public currentTaskHtml: any;
    public alertMessage: string;
    constructor(private http: Http) { }
    private alertMsg = new Subject<string>();
    alertMsg$ = this.alertMsg.asObservable();
    /**
     * This returns a new form component that can be loaded into simulator. The component consists
     * of dynamically created form template from server based on the
     * current task of the given learning instanceid
     * @param lsintid 
     * @param pinstid 
     */
    publishAlertMsg(textToPublish: string) {
        this.alertMsg.next(textToPublish);
    }
    
    getcurrentlearningtask(lsintid: string) {
        console.log('http://localhost:8080/getcurrentlearningtask');
        let params = new URLSearchParams();
        params.set('lpinstid', lsintid);
        return this.http.get('http://localhost:8080/getcurrentlearningtask', { search: params })

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return response.text();
            });
    }
    completeLearningTask(lsintid: string, learningForm: string) {
        let responseJson = "{\"lsinstid\":" + lsintid + ",\"learningform\":" + learningForm + "}";


        let headers = new Headers({ 'Content-Type': 'application/json' }); // ... Set content type to JSON


        return this.http.post('http://localhost:8080/completelearningtask', responseJson) // ...using post request
            .map((res: Response) => { console.log(res); return res.json() }); // ...and calling .json() on the response to return data
        //...errors if any

    }

    logout() {
        // remove user from local storage to log user out
        localStorage.removeItem('currentUser');
    }
}