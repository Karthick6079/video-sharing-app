import { Component, Input, OnInit } from '@angular/core';
import { UserDto, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { LoginService } from '../../services/login/login.service';
import { MessageService } from 'primeng/api';
import { UserService } from '../../services/user/user.service';
import { VideoService } from '../../services/video/video.service';

@Component({
  selector: 'app-channel-info',
  templateUrl: './channel-info.component.html',
  styleUrl: './channel-info.component.css',
  providers: [MessageService],
})
export class ChannelInfoComponent implements OnInit {
  @Input()
  video: VideoDto | undefined;

  public isAuthenticated: boolean = false;
  subscribed: boolean = false;
  unsubscribed: boolean = false;

  currentUser!: UserDto;
  videoUploadedUser!: UserDto;
  subscribersCount = 0;

  constructor(
    private loginService: LoginService,
    private messageService: MessageService,
    private userService: UserService,
    private oidcSecurityService: OidcSecurityService,
    private videoService: VideoService
  ) {}

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );

    this.currentUser = this.userService.getCurrentUser();

    if (
      this.currentUser &&
      this.currentUser.subscribedToUsers.includes(this.video?.userId)
    ) {
      this.subscribed = true;
    }

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
    if (this.showLoginMessageIfNot('Please login to subscribe this channal!')) {
      this.userService
        .unsubscribeUser(String(this.video?.userId))
        .subscribe((isUnsubscribed) => {
          this.subscribed = !isUnsubscribed;
        });
    }
  }
  subscribe() {
    if (this.showLoginMessageIfNot('Please login to subscribe this channal!')) {
      this.userService
        .subscribeUser(String(this.video?.userId))
        .subscribe((isSubscribed) => {
          this.subscribed = isSubscribed;
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
}
