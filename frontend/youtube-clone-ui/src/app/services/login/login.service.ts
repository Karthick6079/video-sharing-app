import { Injectable, OnInit } from '@angular/core';
import { Router, RouterStateSnapshot } from '@angular/router';
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
    private oidcSecurityService: OidcSecurityService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }

  login() {
    let routerState = this.router.routerState.snapshot;
    localStorage.setItem('loginBeforeUrl', routerState.url);
    this.oidcSecurityService.authorize();
  }

  logout() {
    console.log('Logoff method called');

    this.oidcSecurityService.logoffAndRevokeTokens();
    this.oidcSecurityService.logoffLocal();
    localStorage.removeItem('loginBeforeUrl');
    sessionStorage.removeItem('currentUser');
  }
}
