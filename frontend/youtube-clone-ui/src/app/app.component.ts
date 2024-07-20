import { Component, OnInit } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { UserService } from './services/user/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'youtube-clone-ui';

  showSidebarOnOverlay = false;

  constructor(
    private oidcSecurityService: OidcSecurityService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.oidcSecurityService.checkAuth().subscribe(() => {});

    this.userService.showLessSideBarOverlaySubject.subscribe((value) => {
      this.showSidebarOnOverlay = value;
    });
  }

  closeSideBar() {
    this.userService.toggleSideBarOnOverlay();
  }
}
