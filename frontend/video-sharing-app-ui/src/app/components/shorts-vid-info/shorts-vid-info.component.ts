import { Component, Input, OnInit } from '@angular/core';
import { ChannelInfoDTO, UserDto, VideoDto } from '../../dto/video-dto';
import { ConfirmationService, MessageService } from 'primeng/api';
import { UserService } from '../../services/user/user.service';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { LoginService } from '../../services/login/login.service';

@Component({
  selector: 'app-shorts-vid-info',
  templateUrl: './shorts-vid-info.component.html',
  styleUrl: './shorts-vid-info.component.css',
  providers: [ConfirmationService, MessageService],
})
export class ShortsVidInfoComponent implements OnInit {
  @Input()
  video!: VideoDto;

  public isAuthenticated: boolean = false;
  subscribed: boolean = false;
  unsubscribed: boolean = false;

  isLikedVideo: boolean = false;
  disLikedVideo: boolean = false;

  currentUser!: UserDto;
  currentUserFromApiCall!: UserDto;
  videoUploadedUser!: UserDto;
  subscribersCount = 0;
  avatarError = false;

  constructor(
    private messageService: MessageService,
    private userService: UserService,
    private loginService: LoginService,
    private oidcSecurityService: OidcSecurityService,
    private confirmationService: ConfirmationService
  ) {
    this.currentUser = this.userService.getCurrentUser();
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }

  onAvatarError(): void {
    this.avatarError = true;
  }

  getUserInitials(name: string): string {
    if (!name) return '?';
    return name
      .split(' ')
      .map(word => word[0])
      .join('')
      .substring(0, 2)
      .toUpperCase();
  }

  subscribe() {
    if (!this.isUserLoggedIn())
      return;

    this.userService
      .subscribeUser(String(this.video?.userId))
      .subscribe((channalInfo: ChannelInfoDTO) => {
        this.subscribed = channalInfo.userSubscribed;
        this.subscribersCount = channalInfo.subscribersCount;
      });
  }

  showLoginMessageIfNot(message?: string) {
    if (!this.isAuthenticated) {
      this.loginService.login();
    }
    return true;
  }

  isUserLoggedIn() {
    if (!this.isAuthenticated) {
      this.loginService.login();
      return false
    }
    return true;
  }

  setLoginMessage(message: string) {
    this.messageService.add({
      severity: 'info',
      summary: 'Info',
      detail: message,
      sticky: false,
      life: 5000,
      key: 'tc',
    });
  }
}
