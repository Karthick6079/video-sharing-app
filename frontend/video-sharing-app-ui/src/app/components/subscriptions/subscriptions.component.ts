import { Component, OnInit } from '@angular/core';
import { VideoDto } from '../../dto/video-dto';
import { UserService } from '../../services/user/user.service';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { VideoService } from '../../services/video/video.service';

@Component({
  selector: 'app-subscriptions',
  templateUrl: './subscriptions.component.html',
  styleUrl: './subscriptions.component.css',
})
export class SubscriptionsComponent implements OnInit {
  unAuthUIInfotitle = 'Don’t miss new videos';
  unAuthUIInfoDesc = `Sign in to see updates from your favorite channels`;

  getSubscriptionVideos() {
    // throw new Error('Method not implemented.');
  }
  subscribedChannalVideos!: VideoDto[];
  isDataAvailable = false;
  isAuthenticated!: boolean;

  constructor(
    private videoService: VideoService,
    private oidcSecurityService: OidcSecurityService
  ) {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }
  ngOnInit(): void {
    this.videoService.getSubscriptionVideos().subscribe((videos) => {
      if (videos) {
        this.subscribedChannalVideos = videos;
      }

      if (!this.subscribedChannalVideos) {
        this.unAuthUIInfoDesc =
          "Subscribe you're favorite channel to see updates from that";
      }
      this.isDataAvailable = true;
    });
  }
}
