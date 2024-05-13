import { Component, Input, OnInit } from '@angular/core';
import { UserDto, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { LoginService } from '../../services/login/login.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { UserService } from '../../services/user/user.service';
import { VideoService } from '../../services/video/video.service';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { IndianFormatViewCount } from '../../pipes/indianformatviewcount.pipe';

@Component({
  selector: 'app-channel-info',
  templateUrl: './channel-info.component.html',
  styleUrl: './channel-info.component.css',
  providers: [ConfirmationService, MessageService],
})
export class ChannelInfoComponent implements OnInit {
  @Input()
  video: VideoDto | undefined;

  public isAuthenticated: boolean = false;
  subscribed: boolean = false;
  unsubscribed: boolean = false;

  currentUser!: UserDto;
  currentUserFromApiCall!: UserDto;
  videoUploadedUser!: UserDto;
  subscribersCount = 0;

  videoURL!: string;

  isLikedVideo: boolean = false;
  isDisLikedVideo: boolean = false;

  constructor(
    private loginService: LoginService,
    private messageService: MessageService,
    private userService: UserService,
    private oidcSecurityService: OidcSecurityService,
    private videoService: VideoService,
    private confirmationService: ConfirmationService,
    private indianformatviewcount: IndianFormatViewCount
  ) {
    this.currentUser = this.userService.getCurrentUser();
    this.videoURL = window.location.href;
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );

    if (this.currentUser) {
      this.getCurrentUserInfo();
    }
  }

  getCurrentUserInfo() {
    // Get user subscribers details for currentUser;
    this.userService
      .getUserInfoById(this.currentUser.id)
      .subscribe((userDto) => {
        this.currentUserFromApiCall = userDto;
        this.subscribed = this.isCurrentUserSubscribed();
      });

    // Get video uploaded user information details;
    if (this.video) {
      this.userService
        .getUserInfoById(this.video.userId)
        .subscribe((userDto) => {
          this.videoUploadedUser = userDto;
          this.subscribersCount = this.videoUploadedUser.subscribersCount;
        });
    }
  }

  isCurrentUserSubscribed() {
    if (
      this.currentUserFromApiCall &&
      this.currentUserFromApiCall.subscribedToUsers.includes(this.video?.userId)
    ) {
      return true;
    } else {
      return false;
    }
  }

  likeVideo() {
    if (this.showLoginMessageIfNot('Please login to share your feedback!')) {
      this.videoService
        .likeVideo(String(this.video?.id), this.currentUser.id)
        .subscribe((video: VideoDto) => {
          // this.isLikedVideo = !this.isLikedVideo;
          this.video.likes = video.likes;
        });
    }
  }
  dislikeVideo() {
    if (this.showLoginMessageIfNot('Please login to share your feedback!')) {
      this.videoService
        .dislikeVideo(String(this.video?.id), this.currentUser.id)
        .subscribe((video: VideoDto) => {
          this.video.dislikes = video.dislikes;
          // this.isDisLikedVideo = !this.isDisLikedVideo;
        });
    }
  }

  subscribe() {
    if (this.showLoginMessageIfNot('Please login to subscribe this channal!')) {
      this.userService
        .subscribeUser(String(this.video?.userId))
        .subscribe((responseMap: Record<string, any>) => {
          if (responseMap['currentUser']) {
            this.currentUserFromApiCall = responseMap['currentUser'];
          }

          if (responseMap['videoUploadedSubscribersCount'] != undefined) {
            this.subscribersCount =
              responseMap['videoUploadedSubscribersCount'];
          }

          this.subscribed = this.isCurrentUserSubscribed();
        });
    }
  }

  unsubscribe() {
    this.userService
      .unsubscribeUser(String(this.video?.userId))
      .subscribe((responseMap: Record<string, any>) => {
        if (responseMap['currentUser']) {
          this.currentUserFromApiCall = responseMap['currentUser'];
        }

        if (responseMap['videoUploadedSubscribersCount'] != undefined) {
          this.subscribersCount = responseMap['videoUploadedSubscribersCount'];
        }

        this.subscribed = this.isCurrentUserSubscribed();
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

  copyURL() {
    navigator.clipboard.writeText(this.videoURL);
    this.messageService.add({
      severity: 'info',
      summary: 'Copied!',
      detail: 'Video link copied!',
    });
  }

  shareVideoLink() {
    this.confirmationService.confirm({
      message: 'Copy the video link and share it! ',
      key: 'shareConfirmation',
      header: 'Share',
      acceptButtonStyleClass: 'p-button-text p-button-text',
      rejectButtonStyleClass: 'p-button-text p-button-text',
      acceptIcon: 'none',
      rejectIcon: 'none',
      acceptLabel: 'Ok',
      rejectVisible: false,
    });
  }
}
