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

    // if (this.currentUser) {
    //   this.getCurrentUserInfo();
    // }
  }

  // getCurrentUserInfo() {
  //   // Get user subscribers details for currentUser;
  //   this.userService
  //     .getUserInfoById(this.currentUser.id)
  //     .subscribe((userDto) => {
  //       this.currentUserFromApiCall = userDto;
  //       this.subscribed = this.isCurrentUserSubscribed();
  //     });

  //   // Get video uploaded user information details;
  //   if (this.video) {
  //     this.userService
  //       .getUserInfoById(this.video.userId)
  //       .subscribe((userDto) => {
  //         this.videoUploadedUser = userDto;
  //         this.subscribersCount = this.videoUploadedUser.subscribersCount;
  //       });
  //   }
  // }

  // isCurrentUserSubscribed() {
  //   if (
  //     this.currentUserFromApiCall &&
  //     this.currentUserFromApiCall.subscribedToUsers.includes(this.video?.userId)
  //   ) {
  //     return true;
  //   } else {
  //     return false;
  //   }
  // }

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
      if (this.showLoginMessageIfNot('Please login to subscribe this channal!')) {
        this.userService
          .subscribeUser(String(this.video?.userId))
          .subscribe((channalInfo: ChannelInfoDTO) => {
            this.subscribed = channalInfo.isUserSubscribed;
            this.subscribersCount = channalInfo.subscribersCount;
          });
      }
    }

    unsubscribe() {
      this.userService
        .unsubscribeUser(String(this.video?.userId))
        .subscribe((channalInfo: ChannelInfoDTO) => {
          this.subscribed = channalInfo.isUserSubscribed;
          this.subscribersCount = channalInfo.subscribersCount;
        });
    }

  showLoginMessageIfNot(message?: string) {
    if (!this.isAuthenticated) {
      this.loginService.login();
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

  unsubscribeConfirmationPopup() {
    if (this.showLoginMessageIfNot('Please login to subscribe this channal!')) {
      this.confirmationService.confirm({
        // target: event.target as EventTarget,
        key: 'unsubscriberConfirmation',
        message: 'Are you sure that you want to proceed?',
        header: 'Unsubscribe',
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
          this.unsubscribe();
        },
        acceptIcon: 'none',
        rejectIcon: 'none',
        rejectButtonStyleClass: 'p-button-text',
      });
    }
  }
}
