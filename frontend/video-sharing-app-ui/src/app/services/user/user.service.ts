import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { EventEmitter, Injectable, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import {
  ChannelInfoDTO,
  LikedVideoDTO,
  UserDto,
  WatchedVideoDTO,
} from '../../dto/video-dto';
import { OidcSecurityService, UserDataResult } from 'angular-auth-oidc-client';
import { environment } from '../../../environments/environment';

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

  hideSearchBarEventEmitter = new EventEmitter<boolean>();

  hideStudioButtonEventEmitter = new EventEmitter<boolean>();

  playShortsOnFocus = new EventEmitter<boolean>();

  studioStepperIndex = new Subject<number>();

  showLessSideBarSubject = new Subject<boolean>();

  showLessSideBarOverlaySubject = new Subject<boolean>();

  showLessSidebar = false;

  showSidebarOnOverlay = false;

  private currentUserDto!: UserDto;

  isMobileScreen = false;

  ngOnInit(): void {
    this.oidcSecurityService.userData$.subscribe((response) => {
      if (response.userData) {
        this.loggedInUserData = response.userData;
      }
    });
  }

  private getUserBaseUrl(): string {
    return environment.SERVICE_NAME + '/user';
  }

  registerUser(): Observable<UserDto> {
    // const token = this.oidcSecurityService.getAccessToken();
    // const headers = new HttpHeaders({
    //   Authorization: `Bearer ${token}`,
    // });
    return this.http.post<UserDto>(
      this.getUserBaseUrl() + this.REGISTER_URL,
      null,
      // {headers}
    );
  }

  subscribeUser(userId: string): Observable<ChannelInfoDTO> {
    var formData = new FormData();
    formData.append('userId', userId);
    return this.http.put<ChannelInfoDTO>(
      this.getUserBaseUrl() + this.SUBSCRIBE_URL,
      formData
    );
  }

  unsubscribeUser(userId: string): Observable<ChannelInfoDTO> {
    var formData = new FormData();
    formData.append('userId', userId);
    return this.http.put<ChannelInfoDTO>(
      this.getUserBaseUrl() + this.UN_SUBSCRIBE_URL,
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

  getLoggedInUserData() {
    return this.loggedInUserData;
  }

  setCurrentUser(userDto: UserDto) {
    window.sessionStorage.setItem('currentUser', JSON.stringify(userDto));
  }

  getCurrentUser() {
    const currentUserString = window.sessionStorage.getItem('currentUser');
    let currentUser;
    if (currentUserString) {
      currentUser = JSON.parse(currentUserString);
    }

    return currentUser;
  }

  hideSearchBar(input: boolean) {
    this.hideSearchBarEventEmitter.emit(input);
  }

  hideStudioButton(input: boolean) {
    this.hideStudioButtonEventEmitter.emit(input);
  }

  playShorts(input: boolean) {
    this.playShortsOnFocus.emit(input);
  }

  toggleSideBar() {
    this.showLessSidebar = !this.showLessSidebar;
    this.showLessSideBarSubject.next(this.showLessSidebar);
  }
  toggleSideBarOnOverlay() {
    this.showSidebarOnOverlay = !this.showSidebarOnOverlay;
    this.showLessSideBarOverlaySubject.next(this.showSidebarOnOverlay);
  }

  public isMobileScreenFn(): boolean {
    return this.isMobileScreen;
  }
}
