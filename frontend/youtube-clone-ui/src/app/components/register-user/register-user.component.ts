import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-register-user',
  templateUrl: './register-user.component.html',
  styleUrl: './register-user.component.css',
})
export class RegisterUserComponent implements OnInit {
  constructor(private userService: UserService, private router: Router) {}

  isAPILoading = false;

  ngOnInit(): void {
    setTimeout(() => {
      this.registerUserInDB();
    }, 2000);
  }

  registerUserInDB() {
    this.isAPILoading = true;
    this.userService.registerUser().subscribe((userDto) => {
      if (userDto) {
        this.isAPILoading = false;
        this.userService.setCurrentUser(userDto);
        let loginBeforeUrl = localStorage.getItem('loginBeforeUrl');

        if (!loginBeforeUrl) {
          loginBeforeUrl = '/';
        }
        // window.location.assign(loginBeforeUrl);
      }
    });
  }
}
