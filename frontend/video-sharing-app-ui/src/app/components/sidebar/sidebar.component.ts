import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent implements OnInit {
  constructor(private router: Router, private userService: UserService) {}

  showLess = false;

  navigateToUpload() {
    const url = `/studio/edit-video-info/123`;
    this.router.navigateByUrl(url);
  }

  NewMethod() {
    alert('New Method called');
  }
  items: MenuItem[] | undefined;

  ngOnInit() {
    this.items = [
      {
        label: 'Home',
        icon: 'pi pi-fw pi-plus',
      },
      {
        label: 'Shorts',
        icon: 'pi pi-fw pi-trash',
      },
      {
        label: 'Subscriptions',
        icon: 'pi pi-fw pi-plus',
      },
    ];

    this.toggleSideBar();
  }

  toggleSideBar() {
    //show less or more in home page
    // Show sidebar overlay on watch page/other page
    this.userService.showLessSideBarSubject.subscribe((value) => {
      this.showLess = value;
    });
  }
}
