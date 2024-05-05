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
  copyTheURL() {
    throw new Error('Method not implemented.');
  }
  @Input()
  video: VideoDto | undefined;

  public isAuthenticated: boolean = false;
  subscribed: boolean = false;
  unsubscribed: boolean = false;

  currentUser!: UserDto;
  videoUploadedUser!: UserDto;
  subscribersCount = 0;

  videoURL!: string;

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

    // this.currentUser = this.userService.getCurrentUser();

    this.subscribed = this.isCurrentUserSubscribed();

    if (this.video) {
      this.userService
        .getUserInfoById(this.video.userId)
        .subscribe((userDto) => {
          this.videoUploadedUser = userDto;
          if (userDto.subscribers) {
            this.subscribersCount = userDto.subscribers.length;
          }
        });
    }
  }

  isCurrentUserSubscribed() {
    if (
      this.currentUser &&
      this.currentUser.subscribedToUsers.includes(this.video?.userId)
    ) {
      return true;
    } else {
      return false;
    }
  }

  likeVideo() {
    if (this.showLoginMessageIfNot('Please login to share your feedback!')) {
      this.videoService
        .likeVideo(String(this.video?.id))
        .subscribe((video: VideoDto) => {
          this.video = video;
        });
    }
  }
  dislikeVideo() {
    if (this.showLoginMessageIfNot('Please login to share your feedback!')) {
      this.videoService
        .dislikeVideo(String(this.video?.id))
        .subscribe((video: VideoDto) => {
          this.video = video;
        });
    }
  }
  unsubscribe() {
    this.userService
      .unsubscribeUser(String(this.video?.userId))
      .subscribe((userDto) => {
        this.currentUser = userDto;
        this.subscribed = this.isCurrentUserSubscribed();
      });
  }
  subscribe() {
    if (this.showLoginMessageIfNot('Please login to subscribe this channal!')) {
      this.userService
        .subscribeUser(String(this.video?.userId))
        .subscribe((userDto) => {
          this.currentUser = userDto;
          this.subscribed = this.isCurrentUserSubscribed();
          this.messageService.add({
            severity: 'info',
            summary: 'Unsubscribed',
            detail: '',
          });
        });
    }
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
        acceptIcon: 'none',
        rejectIcon: 'none',
        rejectButtonStyleClass: 'p-button-text',
      });

      this.unsubscribe();
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
