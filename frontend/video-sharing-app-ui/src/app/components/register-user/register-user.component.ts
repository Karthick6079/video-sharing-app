import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user/user.service';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-register-user',
  templateUrl: './register-user.component.html',
  styleUrl: './register-user.component.css',
})
export class RegisterUserComponent implements OnInit {
  constructor(private userService: UserService, 
	      private router: Router,
	     private oidcSecurityService: OidcSecurityService) {}

  isAPILoading = false;

  ngOnInit(): void {
  }
}
