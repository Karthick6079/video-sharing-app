import { Component, OnInit } from '@angular/core';
import {
  ConfigUserDataResult,
  OidcSecurityService,
  UserDataResult,
} from 'angular-auth-oidc-client';
import { Observable } from 'rxjs';
import { LoginService } from '../../services/login/login.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  isAuthenticated: boolean = false;
  name: string = '';

  userData!: Observable<UserDataResult>;

  constructor(
    private oidcSecurityService: OidcSecurityService,
    private loginService: LoginService
  ) {}
  ngOnInit(): void {
    this.oidcSecurityService.userData$.subscribe((response) => {
      if (response.userData) {
        this.name = response.userData.name;
      }
    });

    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }

  login() {
    this.loginService.login();
  }
  logout() {
    this.loginService.logout();
  }
}
