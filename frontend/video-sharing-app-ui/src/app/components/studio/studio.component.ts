import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-studio',
  templateUrl: './studio.component.html',
  styleUrl: './studio.component.css',
})
export class StudioComponent implements OnInit, OnDestroy {
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private userService: UserService
  ) {}

  items: MenuItem[] | undefined;

  activeIndex = 0;

  ngOnInit(): void {
    this.items = [
      {
        label: 'Upload',
      },
      {
        label: 'Edit Video Details',
        command: (event: any) => {},
      },
    ];
    this.userService.hideSearchBar(true);
    this.userService.hideStudioButton(true);

    this.userService.studioStepperIndex.subscribe((currentActiveIndex) => {
      this.activeIndex = currentActiveIndex;
    });
  }

  ngOnDestroy(): void {
    this.userService.hideSearchBar(false);
    this.userService.hideStudioButton(false);
  }
}
