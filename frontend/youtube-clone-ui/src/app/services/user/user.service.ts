import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { AppSettings } from '../../constants/AppSettings';
import { Observable } from 'rxjs';
import {
  LikedVideoDTO,
  UserDto,
  VideoDto,
  WatchedVideoDTO,
} from '../../dto/video-dto';
import { OidcSecurityService, UserDataResult } from 'angular-auth-oidc-client';

@Injectable({
  providedIn: 'root',
})
export class UserService implements OnInit {
  private REGISTER_URL: String = '/register';
  private SUBSCRIBE_URL: String = '/subscribe';
  private UN_SUBSCRIBE_URL: String = '/unsubscribe';
  private VIDEO_HISTORY_URL: String = '/videos-history';
  private LIKED_VIDEOS_URL: String = '/liked-videos';

  private loggedInUserData!: UserDataResult;

  constructor(
    private http: HttpClient,
    private oidcSecurityService: OidcSecurityService
  ) {}

  private currentUserDto!: UserDto;

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

  subscribeUser(userId: string): Observable<UserDto> {
    var formData = new FormData();
    formData.append('userId', userId);
    return this.http.put<UserDto>(
      this.getUserBaseUrl() + this.SUBSCRIBE_URL,
      formData
    );
  }

  getUserInfoById(userId: string): Observable<UserDto> {
    return this.http.get<UserDto>(this.getUserBaseUrl() + '/' + userId);
  }

  getWatchedVideos(page: number, size: number): Observable<WatchedVideoDTO[]> {
    let params = new HttpParams().set('page', page).set('size', size);

    const httpOptions = {
      params: params,
    };
    return this.http.get<WatchedVideoDTO[]>(
      this.getUserBaseUrl() + this.VIDEO_HISTORY_URL,
      httpOptions
    );
  }

  getLikedVideos(page: number, size: number): Observable<LikedVideoDTO[]> {
    let params = new HttpParams().set('page', page).set('size', size);

    const httpOptions = {
      params: params,
    };
    return this.http.get<LikedVideoDTO[]>(
      this.getUserBaseUrl() + this.LIKED_VIDEOS_URL,
      httpOptions
    );
  }

  unsubscribeUser(userId: string): Observable<UserDto> {
    var formData = new FormData();
    formData.append('userId', userId);
    return this.http.put<UserDto>(
      this.getUserBaseUrl() + this.UN_SUBSCRIBE_URL,
      formData
    );
  }

  getLoggedInUserData() {
    return this.loggedInUserData;
  }

  setCurrentUser(userDto: UserDto) {
    window.sessionStorage.setItem('currentUser', JSON.stringify(userDto));
  }

  getCurrentUser() {
    const currentUserString = window.sessionStorage.getItem('currentUser');
    let currentUser: UserDto = { subscribedToUsers: [] };
    if (currentUserString) {
      currentUser = JSON.parse(currentUserString);
    }

    return currentUser;
  }
}
