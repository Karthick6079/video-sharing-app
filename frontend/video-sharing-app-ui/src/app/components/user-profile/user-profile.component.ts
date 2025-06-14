import { Component } from '@angular/core';
import { LikedVideoDTO, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { UserService } from '../../services/user/user.service';
import { catchError, Observable, throwError } from 'rxjs';
import _ from 'lodash';
import { KeyValue } from '@angular/common';
import { AdvancedDateGroupService } from '../../services/advanced-date-group.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css',
})
export class UserProfileComponent {
  unAuthUIInfotitle = 'Enjoy your favorite videos';
  unAuthUIInfoDesc = `Sign in to access videos that you’ve liked`;

  likedVideos!: VideoDto[];
  isDataAvailable = false;
  isApiCalling = false;
  isAuthenticated!: boolean;
  page: number = 0;
  size: number = 6;

  groupedVideosRecord: Record<string, LikedVideoDTO[]> = {};
  
  groupedVideosList: { label: string; videos: LikedVideoDTO[] }[] = [];
  

  constructor(
    private userService: UserService,
    private oidcSecurityService: OidcSecurityService,
    private groupingService: AdvancedDateGroupService
  ) {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }

  ngOnInit(): void {
    this.getLikedVideos(true);
  }

  getLikedVideos(isCompLoad: boolean) {
    if (this.isAuthenticated) {
      if (!isCompLoad) {
        this.page = this.page + 1;
      }
      this.isApiCalling = true;
      this.userService
        .getLikedVideos(this.page, this.size).pipe(catchError(
          (error) => {
            this.isApiCalling = false;
            return throwError(() => error);
          }))
        .subscribe((videos) => {
          this.likedVideos = videos;
          this.isApiCalling = false;
          if (videos && videos.length > 0) {
            this.loadVideosOnScroll(videos, "likedAt");
            this.isDataAvailable = true;
          } else {
            this.unAuthUIInfoDesc =
              'You yet to like videos. Like your favorite videos and get personalized recommendations';
          }
        }

        );
    }
  }

  loadVideosOnScroll(videos: LikedVideoDTO[], dateField: string){
  
      const newGroupeVideos = this.groupingService.groupByAdvancedDate(videos, dateField as keyof typeof videos[0]);
      for (const [label, items] of newGroupeVideos) {
        if (this.groupedVideosRecord[label]) {
          this.groupedVideosRecord[label] = [
            ...this.groupedVideosRecord[label],
            ...items
          ];
        } else {
          this.groupedVideosRecord[label] = items;
        }
      }
  
      this.updateGroupedVideosList();
  
    }

    updateGroupedVideosList() {
      this.groupedVideosList = Object.entries(this.groupedVideosRecord).map(
        ([label, videos]) => ({ label, videos })
      );
    }

}
