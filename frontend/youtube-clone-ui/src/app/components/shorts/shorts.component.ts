import { Component, Input, OnInit } from '@angular/core';
import { UserDto, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';

import { MessageService } from 'primeng/api';
import { VideoService } from '../../services/video/video.service';

@Component({
  selector: 'app-shorts',
  templateUrl: './shorts.component.html',
  styleUrl: './shorts.component.css',
})
export class ShortsComponent implements OnInit {
  @Input()
  video!: VideoDto;

  user: UserDto | undefined;

  public isAuthenticated: boolean = false;

  visible: boolean = false;

  constructor(
    private messageService: MessageService,
    private oidcSecurityService: OidcSecurityService,
    private videoService: VideoService
  ) {}

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

  showLoginMessageIfNot(message?: string) {
    if (!this.isAuthenticated) {
      message = message ? message : 'Please login before share your feedback!';
      this.setLoginMessage(message);
      return false;
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
