import { Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
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


  private _playShorts: boolean = false;


  @Input()
  set playShorts(value: boolean) {
    console.log('playShorts changed to:', value)
    console.log('video name', this.video.title);
    this._playShorts = value;
    // this.handlePlayback();
  }

  get playShorts(): boolean {
    return this._playShorts;
  }

  @ViewChild('media') videoPlayerRef!: ElementRef<HTMLVideoElement>;

  user: UserDto | undefined;

  isLikedVideo: boolean = false;
  isDisLikedVideo: boolean = false;

  public isAuthenticated: boolean = false;

  playVideo = false;

  visible: boolean = false;

  currentUser!: UserDto;

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


  // ngOnChanges(changes: SimpleChanges): void {
  //   const videoEl = this.videoPlayerRef?.nativeElement;
  //   // alert("playShorts : "+ this.activeIndex);
  //   if (!videoEl) return;

  //   console.log(this.playShorts)

  //   console.log("playshort latest value", changes['playShorts']);

  //   if (changes['playShorts'] && changes['playShorts'].currentValue === true) {
  //     videoEl.currentTime = 0;
  //     videoEl.play();
  //   } else {
  //     videoEl.pause();
  //   }
  // }

  handlePlayback(): void {
    const videoEl = this.videoPlayerRef?.nativeElement;
    // alert("playShorts : "+ this.activeIndex);
    if (!videoEl) return;

    console.log(this._playShorts)

    // console.log("playshort latest value", changes['playShorts']);

    if (this._playShorts) {
      videoEl.currentTime = 0;
      videoEl.play().catch(() => {
        console.warn("Error occured while video play")
      });
    } else {
      videoEl.pause();
    }
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }

  showComments() {
    this.visible = true;
  }

  likeVideo() {
    if (!this.isUserLoggedIn())
      return;

    this.videoService
      .likeVideo(String(this.video?.id), this.currentUser.id)
      .subscribe((video: VideoDto) => {
        this.isLikedVideo = true;
        this.video.likes = video.likes;
      });
  }
  dislikeVideo() {
    if (!this.isUserLoggedIn())
      return;
    this.videoService
      .dislikeVideo(String(this.video?.id), this.currentUser.id)
      .subscribe((video: VideoDto) => {
        this.isDisLikedVideo = true;
        this.video.dislikes = video.dislikes;
      });
  }

  isUserLoggedIn() {
    if (!this.isAuthenticated) {
      this.loginService.login();
      return false;
    }
    return true;
  }
}

