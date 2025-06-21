import { Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UserDto, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';

import { MessageService } from 'primeng/api';
import { VideoService } from '../../services/video/video.service';
import { LoginService } from '../../services/login/login.service';
import { UserService } from '../../services/user/user.service';
import { ShortsServiceService } from '../../services/shorts-service.service';
import { debounceTime, Subject } from 'rxjs';
import { ReactionResponse } from '../../dto/reaction-response';

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
    this._playShorts = value;
    // this.handlePlayback();
  }

  get playShorts(): boolean {
    return this._playShorts;
  }

  @ViewChild('media') videoPlayerRef!: ElementRef<HTMLVideoElement>;

  user: UserDto | undefined;


  isLiked: boolean = false;
  isDisliked: boolean = false;

  private likeClick$ = new Subject<void>();
  private dislikeClick$ = new Subject<void>();

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

  handlePlayback(): void {
    const videoEl = this.videoPlayerRef?.nativeElement;
    if (!videoEl) return;
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

    this.likeClick$.pipe(debounceTime(400)).subscribe(() => {
      this.handleLike()
    });

    this.dislikeClick$.pipe(debounceTime(400)).subscribe(() => {
      this.handleDislike()
    });

    this.isLiked = this.video.userLiked;
    this.isDisliked = this.video.userDisliked;
  }

  showComments() {
    this.visible = true;
  }

  likeVideo() {
    this.likeClick$.next();
  }

  dislikeVideo() {
    this.dislikeClick$.next();
  }


  handleLike() {
    if (!this.isUserLoggedIn())
      return;

    this.isLiked = !this.isLiked;
    if (this.isLiked) {
      this.isDisliked = false;
    }

    this.videoService
      .likeVideo(String(this.video?.id), this.currentUser.id)
      .subscribe((response: ReactionResponse) => {
        this.video.likes = response.likes;
        this.video.dislikes = response.dislikes;
      });
  }

  handleDislike() {
    if (!this.isUserLoggedIn())
      return;

    this.isDisliked = !this.isDisliked;
    if (this.isDisliked) {
      this.isLiked = false;
    }

    this.videoService
      .dislikeVideo(String(this.video?.id), this.currentUser.id)
      .subscribe((response: ReactionResponse) => {
        this.video.dislikes = response.dislikes;
        this.video.likes = response.likes
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

