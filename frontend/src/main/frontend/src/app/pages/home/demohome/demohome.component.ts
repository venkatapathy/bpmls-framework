import { Component } from '@angular/core';


declare var ADL: any;

@Component({
  selector: 'demohome',
  templateUrl: './demohome.html',
})

export class DemoHome {
  player: YT.Player;
  private id: string = 'UAj4QGNpRhU';

  savePlayer(player) {
    this.player = player;
    console.log('player instance', player);
    console.log(this.player.getVideoUrl());
  }
  onStateChange(event) {
    console.log('player state', event.data);



    var conf = {
      "endpoint": "http://localhost:8090/v1/xAPI/",
      "user": "openlrs",
      "password": "openlrs",
    };
    ADL.XAPIWrapper.changeConfig(conf);
    try{
    var stmt;
    if (event.data == 0) {
      //ended 
      stmt = new ADL.XAPIStatement(
        'mailto:' + localStorage.getItem("currentUser"),
        'http://adlnet.gov/expapi/verbs/completed',
        'https://www.youtube.com/watch?v=UAj4QGNpRhU'
      );
      ADL.XAPIWrapper.sendStatement(stmt, function (resp, obj) {
        ADL.XAPIWrapper.log("[" + obj.id + "]: " + resp.status + " - " + resp.statusText);
      });
    } else if (event.data == 5) {
      //cued
      stmt = new ADL.XAPIStatement(
        'mailto:' + localStorage.getItem("currentUser"),
        'http://adlnet.gov/expapi/verbs/cued',
        'https://www.youtube.com/watch?v=UAj4QGNpRhU'
      );
      ADL.XAPIWrapper.sendStatement(stmt, function (resp, obj) {
        ADL.XAPIWrapper.log("[" + obj.id + "]: " + resp.status + " - " + resp.statusText);
      });
    } else if (event.data == 1) {
      //playing
      stmt = new ADL.XAPIStatement(
        'mailto:' + localStorage.getItem("currentUser"),
        'http://adlnet.gov/expapi/verbs/played',
        'https://www.youtube.com/watch?v=UAj4QGNpRhU'
      );
      ADL.XAPIWrapper.sendStatement(stmt, function (resp, obj) {
        ADL.XAPIWrapper.log("[" + obj.id + "]: " + resp.status + " - " + resp.statusText);
      });
    } else if (event.data == 2) {
      //paused
      stmt = new ADL.XAPIStatement(
        'mailto:' + localStorage.getItem("currentUser"),
        'http://adlnet.gov/expapi/verbs/paused',
        'https://www.youtube.com/watch?v=UAj4QGNpRhU'
      );
      ADL.XAPIWrapper.sendStatement(stmt, function (resp, obj) {
        ADL.XAPIWrapper.log("[" + obj.id + "]: " + resp.status + " - " + resp.statusText);
      });
    }
    }catch(e){
      console.log("Exception in sending statement. E is:"+e);
    }
    //console.log(stmt);


  }
}