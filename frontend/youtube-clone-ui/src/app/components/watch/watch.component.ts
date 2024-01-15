import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VideoDto } from '../../dto/video-dto';
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

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private videoService: VideoService
  ) {
    this.activatedRoute.data.subscribe((data) => {
      this.video = data['video'];
    });
    this.getSuggestionVideos();
  }

  ngOnInit(): void {}

  descPanalOpen = false;

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
