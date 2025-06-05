import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { UserDto, VideoDto } from '../../dto/video-dto';
import { VideoService } from '../../services/video/video.service';
import { Observable, Subscription, of } from 'rxjs';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-watch',
  templateUrl: './watch.component.html',
  styleUrl: './watch.component.css',
})
export class WatchComponent implements OnInit {
  showMoreDescription = false;

  video!: VideoDto;
  videoOb$: Observable<VideoDto | null> = of(null);
  suggestionVideos$!: Observable<VideoDto[]>;
  url!: string;
  sub: Subscription | undefined;
  videoId!: string;
  videoUrl!: string;
  isVideoAvailable: boolean = false;
  descPanalOpen = false;

  currentUser!: UserDto;
  suggestionVideos: VideoDto[] = [];

  page: number = 0;
  private SIZE: number = 6;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private videoService: VideoService,
    private userSevice: UserService
  ) {
    this.activatedRoute.data.subscribe((data) => {
      this.video = data['video'];
    });
    this.currentUser = this.userSevice.getCurrentUser();
    // this.router.
    this.router.routeReuseStrategy.shouldReuseRoute = () => {
      return false;
    };
  }

  ngOnInit(): void {
    this.isVideoAvailable = false;
    this.videoService
      .getSuggestedVideos(
        this.page,
        6 // By default video size 6
      )
      .subscribe((videos) => {
        this.suggestionVideos.push(...videos.filter(item => item.id !== this.video.id));
        this.isVideoAvailable = true;
      });

    //Scroll to top
    window.scrollTo(0, 0);
  }

  closeDesc() {
    this.descPanalOpen = false;
  }

  subsribed: boolean = false;

  onDeactivate(event: Event) {}

  getSuggestionVideos() {
    this.page = this.page + 1;
    this.videoService
      .getSuggestedVideos(this.page, this.SIZE)
      .subscribe((videos) => {
        this.suggestionVideos.push(...videos.filter(item => item.id !== this.video.id));
      });
  }

  toggleDescription() {
    this.showMoreDescription = !this.showMoreDescription;
  }
}
