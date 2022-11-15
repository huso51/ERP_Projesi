import { NgModule } from '@angular/core';
import { BootstrapSwitchComponent } from 'angular2-bootstrap-switch/';
import { CommonModule } from '@angular/common';

@NgModule({
    imports: [CommonModule],
    declarations: [
        BootstrapSwitchComponent,
    ],
    exports: [
        BootstrapSwitchComponent,
    ],

})
export class SwitchModule {}
