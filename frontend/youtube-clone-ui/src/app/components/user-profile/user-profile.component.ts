import { Component } from '@angular/core';
import { LikedVideoDTO, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { UserService } from '../../services/user/user.service';
import { Observable } from 'rxjs';
import _ from 'lodash';
import { KeyValue } from '@angular/common';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css',
})
export class UserProfileComponent {
  watchedVideos!: VideoDto[];
  isDataAvailable = false;
  isAuthenticated!: boolean;
  page: number = 0;
  size: number = 6;
  videosGroupedByDay!: _.Dictionary<LikedVideoDTO[]>;

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

  reverseKeyOrder = (
    a: KeyValue<string, LikedVideoDTO[]>,
    b: KeyValue<string, LikedVideoDTO[]>
  ): number => {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    const lastSevenDays = new Date(today);
    lastSevenDays.setDate(today.getDate() - 7);
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

  watchedVideos$!: Observable<VideoDto[]>;

  ngOnInit(): void {
    this.getLikedVideos(true);
  }

  getLikedVideos(isCompLoad: boolean) {
    if (this.isAuthenticated) {
      if (!isCompLoad) {
        this.page = this.page + 1;
      }
      this.userService
        .getLikedVideos(this.page, this.size)
        .subscribe((videos) => {
          console.log(videos);
          this.watchedVideos = videos;
          this.groupByDays(videos);
          this.isDataAvailable = true;
        });
    }
  }

  groupByDays(videos: LikedVideoDTO[]) {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    const lastSevenDays = new Date(today);
    lastSevenDays.setDate(today.getDate() - 7);

    const groupByDay = _.groupBy(videos, (item) => {
      const itemDate = new Date(item.likedOn);
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

    console.log(this.videosGroupedByDay);
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
