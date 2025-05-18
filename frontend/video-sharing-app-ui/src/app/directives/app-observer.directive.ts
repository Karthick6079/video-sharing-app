import {
  AfterViewInit,
  Directive,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { Subject, debounceTime, filter } from 'rxjs';

@Directive({
  selector: '[appAppObserver]',
})
export class AppObserverDirective implements OnDestroy, OnInit, AfterViewInit {
  @Input() debounceTime = 0;
  @Input() threshold = 0.75;

  @Output() visible = new EventEmitter<HTMLElement>();

  private observer: IntersectionObserver | undefined;
  private subject$ = new Subject<{
    entry: IntersectionObserverEntry;
    observer: IntersectionObserver;
  }>();

  constructor(private element: ElementRef) {}

  videoPlayedElement = undefined;

  ngOnInit() {
    this.createObserver();
  }

  ngAfterViewInit() {
    this.startObservingElements();
    // setTimeout(this.startObservingElements, 100);
  }

  ngOnDestroy() {
    if (this.observer) {
      this.observer.disconnect();
      this.observer = undefined;
    }

    // this.subject$.next();
    this.subject$.complete();
  }

  private isVisible(element: HTMLElement) {
    return new Promise((resolve) => {
      const observer = new IntersectionObserver(([entry]) => {
        resolve(entry.intersectionRatio === 1);
        // observer.disconnect();
      });

      observer.observe(element);
    });
  }

  private createObserver() {
    const options = {
      root: document.querySelector('.shorts-container'),
      rootMargin: '0px',
      threshold: this.threshold,
    };

    const isIntersecting = (entry: IntersectionObserverEntry) =>
      entry.isIntersecting || entry.intersectionRatio > 0;

    const notIntersecting = (entry: IntersectionObserverEntry) =>
      entry.intersectionRatio < 1.0;

    this.observer = new IntersectionObserver((entries, observer) => {
      entries.forEach((entry) => {
        if (isIntersecting(entry)) {
          this.subject$.next({ entry, observer });
        }
      });
    }, options);
  }

  private startObservingElements() {
    if (!this.observer) {
      return;
    }

    this.observer.observe(this.element.nativeElement);

    this.subject$
      .pipe(debounceTime(300), filter(Boolean))
      .subscribe(async ({ entry, observer }) => {
        const target = entry.target as HTMLElement;
        const isStillVisible = await this.isVisible(target);

        if (isStillVisible) {
          this.visible.emit(target);

          let videoElement: HTMLVideoElement =
            this.element.nativeElement.querySelector('#singleVideo');
          videoElement.currentTime = 0;
          videoElement.play();
          this.videoPlayedElement = videoElement;
        } else {
          if (this.videoPlayedElement) {
            this.videoPlayedElement.pause();
          }
        }
      });
  }
}
