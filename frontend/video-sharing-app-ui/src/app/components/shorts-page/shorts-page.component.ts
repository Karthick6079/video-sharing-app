import {
  AfterContentChecked,
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
  Inject,
  NgZone,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { VideoDto } from '../../dto/video-dto';
import { VideoService } from '../../services/video/video.service';
import { DOCUMENT } from '@angular/common';
import { entries } from 'lodash';
import { ShortsServiceService } from '../../services/shorts-service.service';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-shorts-page',
  templateUrl: './shorts-page.component.html',
  styleUrl: './shorts-page.component.css',
})
export class ShortsPageComponent {

  @ViewChild('shortsContainer')
  shortsContainerElement: ElementRef | undefined;


  playShorts: boolean = false;

  playVideo: boolean = false;

  observer: any;

  page: number = 0;
  private SIZE: number = 1;

  activeIndex: number = -1;
 currentlyPlayingVideo?: HTMLVideoElement;

  constructor(
    private videoService: VideoService,
    @Inject(DOCUMENT) private document: Document,
    private cdr: ChangeDetectorRef,
    public userService: UserService,
    private zone: NgZone
  ) {}

  videos!: VideoDto[];
  ngOnInit(): void {
    this.videoService.getShortsVideo(this.page, 2).subscribe((videos) => {
      this.videos = videos;
    });
  }

  getShortVideo() {
    this.page = this.page + 1;
    this.videoService.getShortsVideo(this.page, this.SIZE).subscribe((videos) => {
      this.videos.push(...videos);
    });
  }

  onEnter(el: HTMLElement, index: number) {

    this.activeIndex = index;

    const currentVideoId = this.videos[index].id;

    const video = el.querySelector('video') as HTMLVideoElement;
    if (video) {
      // Pause any previously playing video
      if (this.currentlyPlayingVideo && this.currentlyPlayingVideo !== video) {
        this.currentlyPlayingVideo.pause();
        this.currentlyPlayingVideo.currentTime = 0;
      }

      // Wait until video is ready before playing
      const tryPlay = () => {
        video.play().then(() => {
          this.videoService.updateWatchAndGetVideoDetails(currentVideoId).subscribe((videoDto: VideoDto) => {
            this.videos[index].views = videoDto.views;
            this.videos[index].likes = videoDto.likes;
          });
        }).
          catch((err) => console.warn('Play failed:', err));
      };

      if (video.readyState >= 2) {
        tryPlay();
      } else {
        video.onloadeddata = () => tryPlay();
      }

      this.currentlyPlayingVideo = video;
      this.preloadNextVideo(index + 1);
    }
    
  }
  

  onExit(el: HTMLElement, index: number) {
    const video = el.querySelector('video') as HTMLVideoElement;
    if (video && video !== this.currentlyPlayingVideo) return;
  
    video?.pause();
  }

  preloadNextVideo(index: number) {
    const shortsContainers = document.querySelectorAll('.vid-shorts');
    const nextEl = shortsContainers[index] as HTMLElement;
  
    if (!nextEl) return;
  
    const video = nextEl.querySelector('video') as HTMLVideoElement;
    if (video) {
      video.preload = 'auto';
      // Try loading the video silently
      if (video.readyState < 2) {
        video.load(); // Triggers preload
      }
    }
  }
  
}
