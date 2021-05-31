import { Injectable } from '@angular/core';
import { CanActivate, Router, CanActivateChild, UrlTree, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { LoginService } from './login.service';



@Injectable({
    providedIn: 'root'
})
export class AuthGuardService implements CanActivate, CanActivateChild {

    private isAuthenticated = false;
    constructor(
        private router: Router,
        private loginService: LoginService
    ) { }
    
    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        return this.verifica();
    }
    
    canActivateChild(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        return this.verifica();
    }

    verifica(){
        console.log('logado ', this.loginService.isLoggedIn())
        if(!this.loginService.isLoggedIn()){
            this.router.navigate(['/login']);
            this.isAuthenticated = false;
            return false;
        } else {
            this.isAuthenticated = true;
            return true;
        }
    }
}