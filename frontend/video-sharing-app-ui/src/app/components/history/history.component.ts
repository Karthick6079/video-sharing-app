import { Component, OnInit } from '@angular/core';
import { VideoService } from '../../services/video/video.service';
import { Observable } from 'rxjs';
import { VideoDto, WatchedVideoDTO } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { AuthResult } from 'angular-auth-oidc-client/lib/flows/callback-context';
import { UserService } from '../../services/user/user.service';
import * as _ from 'lodash';
import { KeyValue } from '@angular/common';

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
  sortedDate!: string[];
  videosGroupedByDay!: Record<string, WatchedVideoDTO[]>;
  // mediumDate: string|undefined;

  constructor(
    private userService: UserService,
    private oidcSecurityService: OidcSecurityService
  ) {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }

  watchedVideos$!: Observable<VideoDto[]>;

  ngOnInit(): void {
    this.getWatchedVideos(true);
  }

  reverseKeyOrder = (
    a: KeyValue<string, WatchedVideoDTO[]>,
    b: KeyValue<string, WatchedVideoDTO[]>
  ): number => {
    if (
      a.key === 'Today' ||
      a.key === 'Yesterday' ||
      a.key === 'Last seven days'
    ) {
      return 1;
    } else {
      return Date.parse(b.key) - Date.parse(a.key);
    }
  };

  getWatchedVideos(isCompLoad: boolean) {
    if (this.isAuthenticated) {
      if (!isCompLoad) {
        this.page = this.page + 1;
      }
      this.userService
        .getWatchedVideos(this.page, this.size)
        .subscribe((videos) => {
          videos = videos;
          if (videos && videos.length > 0) {
            this.groupByDays(videos);
            this.isDataAvailable = true;
          } else {
            this.unAuthUIInfoDesc = 'You yet to watch videos!';
          }
        });
    }
  }

  groupByDays(videos: WatchedVideoDTO[]) {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    const lastSevenDays = new Date(today);
    lastSevenDays.setDate(today.getDate() - 7);

    const groupByDay = _.groupBy(videos, (item) => {
      const itemDate = new Date(item.watchedOn);
      if (itemDate.toDateString() === today.toDateString()) {
        return 'Today';
      } else if (itemDate.toDateString() === yesterday.toDateString()) {
        return 'Yesterday';
      } else if (itemDate > lastSevenDays) {
        return 'Last seven days';
      } else {
        return `${itemDate.toLocaleString('default', {
          day: 'numeric',
          month: 'long',
          year: 'numeric',
        })}`;
      }
    });

    this.videosGroupedByDay = this.mergeDictionary(
      this.videosGroupedByDay,
      groupByDay
    );
  }

  mergeDictionary(existing: any, newDict: any) {
    var returnDict: any = {};
    if (existing) {
      for (var key in existing) {
        returnDict[key] = existing[key];
      }
    }

    for (var key in newDict) {
      if (existing && existing[key]) {
        returnDict[key].push(...newDict[key]);
      } else {
        returnDict[key] = newDict[key];
      }
    }
    return returnDict;
  }
}
