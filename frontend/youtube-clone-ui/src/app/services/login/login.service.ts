import { Injectable, OnInit } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Injectable({
  providedIn: 'root',
})
export class LoginService implements OnInit {
  public isAuthenticated: boolean = false;

  constructor(private oidcSecurityService: OidcSecurityService) {}

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }

  login() {
    this.oidcSecurityService.authorize();
  }

  logout() {
    console.log('Logoff method called');
    this.oidcSecurityService.logoffAndRevokeTokens();
    this.oidcSecurityService.logoffLocal();
  }
}
