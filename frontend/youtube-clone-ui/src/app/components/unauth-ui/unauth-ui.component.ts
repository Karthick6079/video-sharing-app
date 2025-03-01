import { Component, Input, OnInit } from '@angular/core';
import { LoginService } from '../../services/login/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-unauth-ui',
  templateUrl: './unauth-ui.component.html',
  styleUrl: './unauth-ui.component.css',
})
export class UnauthUiComponent implements OnInit {
  constructor(private loginService: LoginService, private router: Router) {}

  @Input()
  materialIconName: string = 'highlight_off';

  @Input()
  infoTextTitle: string = 'Unauthorized Access';

  @Input()
  infoTextDesc: string = 'Please sign in and access the content';

  @Input()
  toHome: boolean = false;

  ngOnInit(): void {
    // throw new Error('Method not implemented.');
  }

  signin() {
    this.loginService.login();
  }

  navigateToHome() {
    this.router.navigateByUrl('/');
  }
}
