import { Injectable } from '@angular/core';
import { Router, CanActivate,CanLoad } from '@angular/router';
 
@Injectable()
export class AuthGuard implements CanActivate {
 
    constructor(private router: Router) { }
 
    canActivate() {
        if (localStorage.getItem('currentUser')) {
            // logged in so return true
            return true;
        }
        // not logged in so redirect to login page
        this.router.navigate(['login']);
        return false;
    }
    canLoad() {
        if (!localStorage.getItem('currentUser')) {
            // logged in so return true
            return true;
        } else {
            this.router.navigate(['pages/dashboard']);
        }
    }
}
