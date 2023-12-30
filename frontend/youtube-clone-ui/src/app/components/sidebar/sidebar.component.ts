import { Component } from '@angular/core';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
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
