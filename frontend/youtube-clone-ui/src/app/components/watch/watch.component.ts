import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { UserDto, VideoDto } from '../../dto/video-dto';
import { VideoService } from '../../services/video/video.service';
import {
  Observable,
  Subscription,
  filter,
  map,
  of,
  switchMap,
  take,
} from 'rxjs';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-watch',
  templateUrl: './watch.component.html',
  styleUrl: './watch.component.css',
})
export class WatchComponent implements OnInit, OnDestroy {
  video!: VideoDto;
  videoOb$: Observable<VideoDto | null> = of(null);
  suggestionVideos$!: Observable<VideoDto[]>;
  url!: string;
  sub: Subscription | undefined;
  videoId!: string;
  videoUrl!: string;
  isVideoAvailable: boolean = false;
  descPanalOpen = false;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private videoService: VideoService,
    private userSevice: UserService
  ) {
    this.activatedRoute.data.subscribe((data) => {
      this.video = data['video'];
    });
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

  ngOnInit(): void {
    this.getSuggestionVideos();

    this.activatedRoute.params.subscribe((params) =>
      console.log(params['videoId'])
    );

    console.log(this.video);
  }

  closeDesc() {
    this.descPanalOpen = false;
    console.log('Deactivate method called from closeDesc');
  }

  subsribed: boolean = false;

  onDeactivate(event: Event) {
    console.log('Deactivate method called');
  }

  getSuggestionVideos() {
    this.suggestionVideos$ = this.videoService.getVideos();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}
