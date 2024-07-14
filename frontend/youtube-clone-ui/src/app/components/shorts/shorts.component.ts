import { Component, Input, OnInit } from '@angular/core';
import { UserDto, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';

import { MessageService } from 'primeng/api';
import { VideoService } from '../../services/video/video.service';
import { LoginService } from '../../services/login/login.service';
import { UserService } from '../../services/user/user.service';
import { ShortsServiceService } from '../../services/shorts-service.service';

@Component({
  selector: 'app-shorts',
  templateUrl: './shorts.component.html',
  styleUrl: './shorts.component.css',
})
export class ShortsComponent implements OnInit {
  @Input()
  video!: VideoDto;

  @Input()
  playShorts: boolean = false;

  user: UserDto | undefined;

  isLikedVideo: boolean = false;
  isDisLikedVideo: boolean = false;

  public isAuthenticated: boolean = false;

  playVideo = false;

  visible: boolean = false;

  currentUser!: UserDto;

  isMobileScreen = false;

  constructor(
    private messageService: MessageService,
    private oidcSecurityService: OidcSecurityService,
    private videoService: VideoService,
    private loginService: LoginService,
    private userService: UserService,
    private shortsService: ShortsServiceService
  ) {
    this.currentUser = this.userService.getCurrentUser();
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );

    // this.userService.playShortsOnFocus.subscribe((playVideo) => {
    //   console.log('Hii222');
    //   this.playVideo = playVideo;
    // });
    // this.shortsService.playShortsSubject.subscribe((playVideo) => {
    //   this.playVideo = playVideo;
    // });

    if (window.matchMedia('(max-width: 767px)').matches) {
      this.isMobileScreen = true;
    }
  }

  showComments() {
    this.visible = true;
  }

  likeVideo() {
    if (this.showLoginMessageIfNot('Please login to share your feedback!')) {
      this.videoService
        .likeVideo(String(this.video?.id), this.currentUser.id)
        .subscribe((video: VideoDto) => {
          this.isLikedVideo = true;
          this.video = video;
        });
    }
  }
  dislikeVideo() {
    if (this.showLoginMessageIfNot('Please login to share your feedback!')) {
      this.videoService
        .dislikeVideo(String(this.video?.id), this.currentUser.id)
        .subscribe((video: VideoDto) => {
          this.isDisLikedVideo = true;
          this.video = video;
        });
    }
  }

  showLoginMessageIfNot(message?: string) {
    if (!this.isAuthenticated) {
      // message = message ? message : 'Please login before share your feedback!';
      // this.setLoginMessage(message);
      // return false;
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
