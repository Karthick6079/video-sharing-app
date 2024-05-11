import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  constructor(private router: Router) {}

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
  }
}
