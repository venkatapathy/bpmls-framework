import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'
import { Subject } from 'rxjs/Subject';

@Injectable()
export class LearningEngineService {
    public currentTaskHtml: any;
    public alertMessage: string;
    private alertMsg = new Subject<string>();
    alertMsg$ = this.alertMsg.asObservable();

    constructor(private http: Http) { }

    getavailablelearningpaths() {
        //console.log('http://localhost:8080/getavailablelearningpaths');
        
        return this.http.get('http://localhost:8080/getavailablelearningpaths')

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return JSON.parse(response.text());
            });
    }

     publishAlertMsg(textToPublish: string) {
        this.alertMsg.next(textToPublish);
    }
    
    getcurrentlearningtask(lpintid: string) {
        console.log('http://localhost:8080/getcurrentlearningtask/'+lpintid);
        let params = new URLSearchParams();
        params.set('lpinstid', lpintid);
        return this.http.get('http://localhost:8080/getcurrentlearningtask/'+lpintid, { search: params })

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

    startaLearningPath(lpid:string){
        let responseJson = "{\"lpid\":\"" + lpid +"\"}";


        let headers = new Headers({ 'Content-Type': 'application/json' });
        let params = new URLSearchParams();
        
        console.log("Starting a learning path for: "+lpid);

        return this.http.post('http://localhost:8080/startalearningpath/'+lpid, responseJson)

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return response.json();
            });
    }

    startalearningscenario(lpid:string,lpinstd:string){
         let responseJson = "{\"lpid\":\"" + lpid +"\"}";


        let headers = new Headers({ 'Content-Type': 'application/json' });
        let params = new URLSearchParams();
        
        console.log("Starting a learning path for: "+lpid);

        return this.http.post('http://localhost:8080/startalearningscenario/'+lpid+"/"+lpinstd, responseJson)

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return response.json();
            });
    }

}