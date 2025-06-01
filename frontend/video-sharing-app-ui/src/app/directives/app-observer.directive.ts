import {
  AfterViewInit,
  Directive,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { debounceTime, Subject } from 'rxjs';
@Directive({
  selector: '[appAppObserver]',
})
export class AppObserverDirective implements AfterViewInit, OnDestroy {
  @Input() threshold = 1.0;
  @Output() entered = new EventEmitter<HTMLElement>();
  @Output() exited = new EventEmitter<HTMLElement>();


  private entered$ = new Subject<HTMLElement>();
  private exited$ = new Subject<HTMLElement>();

  private observer!: IntersectionObserver;

  constructor(private el: ElementRef) {}

  ngAfterViewInit() {
    this.observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && entry.intersectionRatio >= 0.75) {
          this.entered$.next(this.el.nativeElement);
        } else {
          this.exited$.next(this.el.nativeElement);
        }
      },
      {
        threshold: this.threshold,
        root: document.querySelector('.shorts-container'),
      }
    );

    this.entered$
    .pipe(debounceTime(100))
    .subscribe((el) => this.entered.emit(el));

  this.exited$
    .pipe(debounceTime(100))
    .subscribe((el) => this.exited.emit(el));

    this.observer.observe(this.el.nativeElement);
  }

  ngOnDestroy() {
    if (this.observer) {
      this.observer.disconnect();
    }
  }
}
