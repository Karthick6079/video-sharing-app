import { Component, OnInit } from '@angular/core';
import { VideoService } from '../../services/video/video.service';
import { Observable } from 'rxjs';
import { VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { AuthResult } from 'angular-auth-oidc-client/lib/flows/callback-context';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrl: './history.component.css',
})
export class HistoryComponent implements OnInit {
  watchedVideos!: VideoDto[];
  isDataAvailable = false;
  isAuthenticated!: boolean;

  constructor(
    private userService: UserService,
    private oidcSecurityService: OidcSecurityService
  ) {}

  watchedVideos$!: Observable<VideoDto[]>;

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );

    setTimeout(this.getWatchedVideos, 2000);
  }

  getWatchedVideos() {
    if (this.isAuthenticated) {
      this.userService.getWatchedVideos().subscribe((videos) => {
        console.log(videos);
        this.watchedVideos = videos;
        this.isDataAvailable = true;
      });
    }
  }
}
