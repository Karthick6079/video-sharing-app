import { Injectable, OnInit } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';
import {
  LogoutAuthOptions,
  OidcSecurityService,
} from 'angular-auth-oidc-client';

@Injectable({
  providedIn: 'root',
})
export class LoginService implements OnInit {
  public isAuthenticated: boolean = false;

  constructor(
    private oidcSecurityService: OidcSecurityService // private routerSnap: ActivatedRouteSnapshot
  ) {}

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }

  login() {
    localStorage.setItem('loginBeforeUrl', window.location.href);
    this.oidcSecurityService.authorize();
  }

  logout() {
    console.log('Logoff method called');

    this.oidcSecurityService.logoffAndRevokeTokens();
    this.oidcSecurityService.logoffLocal();
    localStorage.removeItem('loginBeforeUrl');
  }
}
