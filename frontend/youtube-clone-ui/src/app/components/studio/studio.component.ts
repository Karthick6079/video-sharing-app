import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-studio',
  templateUrl: './studio.component.html',
  styleUrl: './studio.component.css',
})
export class StudioComponent implements OnInit {
  items: MenuItem[] | undefined;

  ngOnInit(): void {
    this.items = [
      {
        label: 'Upload',
        routerLink: 'upload',
      },
      {
        label: 'Edit Video Details',
        routerLink: 'editMetadata',
      },
      {
        label: 'Review and Publish',
        routerLink: 'review-and-publish',
      },
    ];
  }
}
