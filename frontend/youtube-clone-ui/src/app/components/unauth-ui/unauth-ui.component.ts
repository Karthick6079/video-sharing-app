import { Component, Input, OnInit } from '@angular/core';
import { LoginService } from '../../services/login/login.service';

@Component({
  selector: 'app-unauth-ui',
  templateUrl: './unauth-ui.component.html',
  styleUrl: './unauth-ui.component.css',
})
export class UnauthUiComponent implements OnInit {
  constructor(private loginService: LoginService) {}

  @Input()
  materialIconName!: string;

  @Input()
  infoTextTitle!: string;

  @Input()
  infoTextDesc!: string;

  ngOnInit(): void {
    // throw new Error('Method not implemented.');
  }

  signin() {
    this.loginService.login();
  }
}
