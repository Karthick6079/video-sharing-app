import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppSettings } from '../../constants/AppSettings';
import { Observable } from 'rxjs';
import { UserDto } from '../../dto/video-dto';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private REGISTER_URL: String = '/register';

  constructor(private http: HttpClient) {}

  private getUserBaseUrl(): string {
    return AppSettings.HOST + AppSettings.SERVICE_NAME + '/user';
  }

  registerUser(): Observable<UserDto> {
    return this.http.post<UserDto>(
      this.getUserBaseUrl() + this.REGISTER_URL,
      null
    );
  }
}
