import {
  AfterContentChecked,
  AfterViewChecked,
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  Inject,
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
export class ShortsPageComponent
  implements OnInit, AfterViewInit, AfterViewChecked
{
  @ViewChild('shortsContainer')
  shortsContainerElement: ElementRef | undefined;

  @ViewChildren('shortsContainer')
  shortsElements: QueryList<ElementRef>;

  playShorts: boolean = false;

  observer: any;

  constructor(
    private videoService: VideoService,
    @Inject(DOCUMENT) private document: Document,
    private shortsService: ShortsServiceService,
    public userService: UserService
  ) {}

  ngAfterViewInit(): void {
    console.log(this.shortsContainerElement.nativeElement);
    // this.callIntersectionObserver();
  }

  callIntersectionObserver() {
    setTimeout(this.registerObserver, 2000);
  }

  registerObserver() {
    const options = {
      root: null,
      threshold: 0.5,
    };

    console.log(this.document.querySelectorAll('.vid-shorts'));
    this.observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting) {
        // this.userService.playShorts(true);
        console.log('The current shorts on foucs');
        console.log(entries[0].target);
      }
    }, options);

    this.document.querySelectorAll('.vid-shorts').forEach((element) => {
      this.observer.observe(element);
    });
  }

  ngAfterViewChecked(): void {
    // const options = {
    //   root: this.shortsContainerElement,
    //   threshold: 0.5,
    // };
    // let observer = new IntersectionObserver(this.playShortsVideo);
    // this.document.querySelectorAll('.vid-shorts').forEach((element) => {
    //   observer.observe(element);
    // });
  }

  videos!: VideoDto[];
  ngOnInit(): void {
    this.videoService.getShortsVideo().subscribe((videos) => {
      this.videos = videos;
      // this.registerObserver();
    });
  }

  getShortVideo() {
    this.videoService.getShortsVideo().subscribe((videos) => {
      this.videos.push(...videos);
      // this.callIntersectionObserver();
    });
  }

  onVisible() {
    // console.log('From compone');
    // this.playShorts = true;
    // this.shortsService.playShortsVideo(true);
    // this.userService.playShortsOnFocus.emit(true);
  }
}
