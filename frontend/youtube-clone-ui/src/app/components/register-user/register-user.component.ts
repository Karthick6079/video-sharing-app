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

  ngOnInit(): void {
    setTimeout(() => {
      this.registerUserInDB();
    }, 1500);
  }

  registerUserInDB() {
    this.userService.registerUser().subscribe((userDto) => {
      if (userDto) {
        console.log(userDto);
        this.router.navigate(['home']);
      }
    });
  }
}
