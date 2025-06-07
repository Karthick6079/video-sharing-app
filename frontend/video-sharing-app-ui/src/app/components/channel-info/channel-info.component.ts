import { Component, Input, OnInit } from '@angular/core';
import { ChannelInfoDTO, UserDto, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { LoginService } from '../../services/login/login.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { UserService } from '../../services/user/user.service';
import { VideoService } from '../../services/video/video.service';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { IndianFormatViewCount } from '../../pipes/indianformatviewcount.pipe';
import { debounceTime, Subject } from 'rxjs';
import { ReactionResponse } from '../../dto/reaction-response';

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

  isLiked: boolean = false;
  isDisliked: boolean = false;
  avatarError = false;

  private likeClick$ = new Subject<void>();
  private dislikeClick$ = new Subject<void>();


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

    this.subscribersCount = this.video.channelSubscribersCount;
    this.subscribed = this.video.isCurrentUserSubscribedToChannel;


    this.likeClick$.pipe(debounceTime(400)).subscribe(() => {
      this.handleLike()
    });

    this.dislikeClick$.pipe(debounceTime(400)).subscribe(() => {
      this.handleDislike()
    });

    this.isLiked = this.video.userLiked;
    this.isDisliked = this.video.userDisliked;
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

  unsubscribe() {

    if (!this.isUserLoggedIn())
      return;

    this.userService
      .unsubscribeUser(String(this.video?.userId))
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

  unsubscribeConfirmationPopup() {
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
}
