import { Component, OnInit } from '@angular/core';
import {
  ConfigUserDataResult,
  OidcSecurityService,
  UserDataResult,
} from 'angular-auth-oidc-client';
import { Observable } from 'rxjs';
import { LoginService } from '../../services/login/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  isAuthenticated: boolean = false;
  name: string = '';
  userPictureUrl!: string;

  isStudio = false;

  userData!: Observable<UserDataResult>;

  constructor(
    private oidcSecurityService: OidcSecurityService,
    private loginService: LoginService,
    private router: Router
  ) {}
  ngOnInit(): void {
    this.oidcSecurityService.userData$.subscribe((response) => {
      if (response.userData) {
        this.name = response.userData.name;
        this.userPictureUrl = response.userData.picture;
      }
    });

    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );

    const url = this.router.url;

    if (url.indexOf('studio') != -1) {
      this.isStudio = true;
    }

    console.log(this.userData);
  }

  login() {
    this.loginService.login();
  }
  logout() {
    this.loginService.logout();
  }

  navigateToHome() {
    window.location.assign(window.location.origin);
  }

  searchVideos(searchText: string) {
    this.router
      .navigateByUrl('/home/search-results?searchText=' + searchText)
      .then((e) => {
        if (e) {
          console.log('Navigation success!!');
        } else {
          console.log('Navigation failed!!');
        }
      });

    // this.router.navigate(['home', 'search-results'], {
    //   queryParams: { searchText: searchText },
    // });
  }
}
