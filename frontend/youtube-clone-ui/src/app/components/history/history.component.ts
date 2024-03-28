import { Component, OnInit } from '@angular/core';
import { VideoService } from '../../services/video/video.service';
import { Observable } from 'rxjs';
import { VideoDto, WatchedVideoDTO } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { AuthResult } from 'angular-auth-oidc-client/lib/flows/callback-context';
import { UserService } from '../../services/user/user.service';
import * as _ from 'lodash';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrl: './history.component.css',
})
export class HistoryComponent implements OnInit {
  watchedVideos!: VideoDto[];
  isDataAvailable = false;
  isAuthenticated!: boolean;
  page: number = 0;
  size: number = 6;
  videosGroupedByDay!: _.Dictionary<WatchedVideoDTO[]>;

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

  getWatchedVideos(isCompLoad: boolean) {
    if (this.isAuthenticated) {
      if (!isCompLoad) {
        this.page = this.page + 1;
      }
      this.userService
        .getWatchedVideos(this.page, this.size)
        .subscribe((videos) => {
          console.log(videos);
          this.watchedVideos = videos;
          this.groupByDays(videos);
          this.isDataAvailable = true;
        });
    }
  }

  groupByDays(videos: WatchedVideoDTO[]) {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    const lastSevenDays = new Date(today);
    lastSevenDays.setDate(today.getDate() - 7);

    const groupByDay = _.mapValues(
      _.groupBy(videos, (item) => {
        const itemDate = new Date(item.watchedOn);
        return `${itemDate.toLocaleString('default', {
          day: 'numeric',
          month: 'long',
          year: 'numeric',
        })}`;
      }),
      (v) => _.sortBy(v, 'desc')
    );

    this.videosGroupedByDay = this.mergeDictionary(
      this.videosGroupedByDay,
      groupByDay
    );

    console.log(this.videosGroupedByDay);

    console.log('------------------------------');

    const sortedObject = _.orderBy(this.videosGroupedByDay, 'desc');

    console.log(sortedObject);
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
