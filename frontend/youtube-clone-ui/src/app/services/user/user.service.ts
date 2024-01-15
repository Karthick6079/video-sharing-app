import { HttpClient } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { AppSettings } from '../../constants/AppSettings';
import { Observable } from 'rxjs';
import { UserDto } from '../../dto/video-dto';
import { OidcSecurityService, UserDataResult } from 'angular-auth-oidc-client';

@Injectable({
  providedIn: 'root',
})
export class UserService implements OnInit {
  private REGISTER_URL: String = '/register';
  private SUBSCRIBE_URL: String = '/subscribe';
  private UN_SUBSCRIBE_URL: String = '/unsubscribe';

  private loggedInUserData!: UserDataResult;

  constructor(
    private http: HttpClient,
    private oidcSecurityService: OidcSecurityService
  ) {}

  ngOnInit(): void {
    this.oidcSecurityService.userData$.subscribe((response) => {
      if (response.userData) {
        this.loggedInUserData = response.userData;
      }
    });
  }

  private getUserBaseUrl(): string {
    return AppSettings.HOST + AppSettings.SERVICE_NAME + '/user';
  }

  registerUser(): Observable<UserDto> {
    return this.http.post<UserDto>(
      this.getUserBaseUrl() + this.REGISTER_URL,
      null
    );
  }

  subscribeUser(userId: string): Observable<boolean> {
    var formData = new FormData();
    formData.append('userId', userId);
    return this.http.put<boolean>(
      this.getUserBaseUrl() + this.SUBSCRIBE_URL,
      formData
    );
  }

  unsubscribeUser(userId: string): Observable<boolean> {
    var formData = new FormData();
    formData.append('userId', userId);
    return this.http.put<boolean>(
      this.getUserBaseUrl() + this.UN_SUBSCRIBE_URL,
      formData
    );
  }

  getLoggedInUserData() {
    return this.loggedInUserData;
  }
}
