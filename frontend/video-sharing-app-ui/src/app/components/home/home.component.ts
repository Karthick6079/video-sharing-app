import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  showLessSideBar = false;

  constructor(
    private router: Router,
    private activateRoute: ActivatedRoute,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    // this.router.navigate(['./featured'], { relativeTo: this.activateRoute });

    this.userService.showLessSideBarSubject.subscribe((value) => {
      this.showLessSideBar = value;
    });
  }
}
