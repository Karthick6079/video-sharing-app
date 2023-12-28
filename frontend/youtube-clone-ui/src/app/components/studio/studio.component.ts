import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-studio',
  templateUrl: './studio.component.html',
  styleUrl: './studio.component.css',
})
export class StudioComponent implements OnInit {
  constructor(private router: Router, private activatedRoute: ActivatedRoute) {}

  items: MenuItem[] | undefined;

  ngOnInit(): void {
    this.items = [
      {
        label: 'Upload',
        routerLink: 'upload',
      },
      {
        label: 'Edit Video Details',
        routerLink: 'edit-video-info',
      },
      // {
      //   label: 'Review and Publish',
      //   routerLink: 'review-and-publish',
      // },
    ];
    console.log(this.router.url);
    this.router
      .navigate(['./upload'], { relativeTo: this.activatedRoute })
      .then((value) => {
        console.log('Navigate successfull');
      });
  }
}
