import { Component, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { AppSettings } from './app.settings';
import { Settings } from './app.settings.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent {
    public settings: Settings;
    options = {
        position: ['bottom', 'right'],
        timeOut: 5000,
        lastOnBottom: true,
      };
    constructor(public appSettings:AppSettings, private router:Router){
        this.settings = this.appSettings.settings;  
    }    


    /* These following methods used for theme preview, you can remove this methods */
    
    // ngOnInit() { 
    //     var demo = this.getParameterByName('demo');
    //     this.setLayout(demo);
    // }
   
}
