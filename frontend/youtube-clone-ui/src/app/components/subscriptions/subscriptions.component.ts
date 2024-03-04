import { Component } from '@angular/core';
import { VideoDto } from '../../dto/video-dto';
import { UserService } from '../../services/user/user.service';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-subscriptions',
  templateUrl: './subscriptions.component.html',
  styleUrl: './subscriptions.component.css',
})
export class SubscriptionsComponent {
  getSubscriptionVideos() {
    throw new Error('Method not implemented.');
  }
  subscribedChannalVideos!: VideoDto[];
  isDataAvailable = false;
  isAuthenticated!: boolean;

  constructor(
    private userService: UserService,
    private oidcSecurityService: OidcSecurityService
  ) {
    this.userService.getWatchedVideos().subscribe((videos) => {
      console.log(videos);
      this.subscribedChannalVideos = videos;
      this.isDataAvailable = true;
    });
  }
}
