import {
  AfterContentChecked,
  AfterViewChecked,
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  Inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { VideoDto } from '../../dto/video-dto';
import { VideoService } from '../../services/video/video.service';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-shorts-page',
  templateUrl: './shorts-page.component.html',
  styleUrl: './shorts-page.component.css',
})
export class ShortsPageComponent implements OnInit {
  @ViewChild('shorts')
  shortsElement: ElementRef | undefined;

  constructor(
    private videoService: VideoService,
    @Inject(DOCUMENT) private document: Document
  ) {}

  testNewMethod() {
    let shortsContainer = this.document.querySelector('.shorts-container');

    if (!this.shortsElement) {
      return;
    }

    const options = {
      root: shortsContainer,
      threshold: 0.5,
    };

    // const threshold = 0.2; // how much % of the element is in view
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          console.log('Intersecting observer working..!');
          // run your animation code here
          // observer.disconnect(); // disconnect if you want to stop observing else it will rerun every time its back in view. Just make sure you disconnect in ngOnDestroy instead
        }
      });
    }, options);
    observer.observe(this.shortsElement.nativeElement);
  }

  // ngAfterViewInit() {
  //   let shortsContainer = this.document.querySelector('.shorts-container');

  //   if (!this.shortsElement) {
  //     this.shortsElement = new ElementRef('');
  //   }

  //   const options = {
  //     root: shortsContainer,
  //     threshold: 0.5,
  //   };

  //   // const threshold = 0.2; // how much % of the element is in view
  //   const observer = new IntersectionObserver((entries) => {
  //     entries.forEach((entry) => {
  //       if (entry.isIntersecting) {
  //         console.log('Intersecting observer working..!');
  //         // run your animation code here
  //         // observer.disconnect(); // disconnect if you want to stop observing else it will rerun every time its back in view. Just make sure you disconnect in ngOnDestroy instead
  //       }
  //     });
  //   }, options);
  //   observer.observe(this.shortsElement.nativeElement);
  // }

  videos!: VideoDto[];
  ngOnInit(): void {
    this.videoService.getShortsVideo().subscribe((videos) => {
      this.videos = videos;
      setTimeout(this.testNewMethod, 2000);
    });

    // this.testMethod();
  }

  getShortVideo() {
    console.log('scroll-event-called..');
    this.videoService.getShortsVideo().subscribe((videos) => {
      this.videos.push(...videos);
    });
  }

  testMethod() {
    let shorts = this.document.querySelector('.vid-shorts');

    if (!shorts) {
      shorts = new Element();
    }

    const observer = new window.IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          console.log('Short on FOUCS');
          return;
        }
        console.log('Short on not FOUCS');
      },
      {
        root: null,
        threshold: 0.5, // set offset 0.1 means trigger if atleast 10% of element in viewport
      }
    );

    observer.observe(shorts);
  }
}
