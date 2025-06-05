import { Component, OnInit } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { UserService } from './services/user/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'video-sharing-app-ui';

  showSidebarOnOverlay = false;

  LOGIN_BEFORE_URL = 'loginBeforeUrl';

  constructor(
    private oidcSecurityService: OidcSecurityService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userService.showLessSideBarOverlaySubject.subscribe((value) => {
      this.showSidebarOnOverlay = value;
    });

    this.oidcSecurityService.checkAuth().subscribe(({ isAuthenticated }) => {
      console.log("isAuthenticated: ", isAuthenticated);
      if (isAuthenticated) {
        this.registerUserInDB();
      }
    })
    
  }

  registerUserInDB() {
    this.userService.registerUser().subscribe((userDto) => {
      if (userDto) {
        this.userService.setCurrentUser(userDto);
        let loginBeforeUrl = localStorage.getItem(this.LOGIN_BEFORE_URL);

        if (loginBeforeUrl) {
          localStorage.removeItem(this.LOGIN_BEFORE_URL); // removing login before url after sign navigate
          this.router.navigateByUrl(loginBeforeUrl);
        }
        // window.location.assign(loginBeforeUrl);
      }
    });
  }

  closeSideBar() {
    this.userService.toggleSideBarOnOverlay();
  }
}
