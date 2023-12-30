import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  constructor(private router: Router, private activateRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.router.navigate(['./featured'], { relativeTo: this.activateRoute });
  }
}
