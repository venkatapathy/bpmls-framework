import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'
import { Subject } from 'rxjs/Subject';

@Injectable()
export class LearningEngineService {

    constructor(private http: Http) { }

    getavailablelearningpaths() {
        //console.log('http://localhost:8080/getavailablelearningpaths');
        
        return this.http.get('http://localhost:8080/getavailablelearningpaths')

            .map((response: Response) => {
                // login successful if there's a jwt token in the response

                return JSON.parse(response.text());
            });
    }
}