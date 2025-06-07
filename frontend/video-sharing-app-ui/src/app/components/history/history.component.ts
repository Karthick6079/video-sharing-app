import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { VideoDto, WatchedVideoDTO } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { UserService } from '../../services/user/user.service';
import { KeyValue } from '@angular/common';
import { AdvancedDateGroupService } from '../../services/advanced-date-group.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrl: './history.component.css',
})
export class HistoryComponent implements OnInit {
  unAuthUIInfotitle = 'Keep track of what you watch';
  unAuthUIInfoDesc = `Watch history isn't viewable when signed out`;

  watchedVideos!: VideoDto[];
  isDataAvailable = false;
  isAuthenticated!: boolean;
  page: number = 0;
  size: number = 6;
  videos!: WatchedVideoDTO[];

  groupedVideosRecord: Record<string, WatchedVideoDTO[]> = {};

  groupedVideosList: { label: string; videos: WatchedVideoDTO[] }[] = [];

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
    this.getWatchedVideos(true);
  }

  getWatchedVideos(isCompLoad: boolean) {
    if (this.isAuthenticated) {
      if (!isCompLoad) {
        this.page = this.page + 1;
      }
      this.userService
        .getWatchedVideos(this.page, this.size)
        .subscribe((videos) => {
          // videos = videos;
          if (videos && videos.length > 0) {
            this.loadVideosOnScroll(videos, "watchedAt");
            this.isDataAvailable = true;
          } else {
            this.unAuthUIInfoDesc = 'You yet to watch videos!';
          }
        });
    }
  }


  loadVideosOnScroll(videos: WatchedVideoDTO[], dateField: string){

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
