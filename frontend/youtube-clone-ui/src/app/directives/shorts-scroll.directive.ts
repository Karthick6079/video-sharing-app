import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appShortsScroll]',
})
export class ShortsScrollDirective {
  constructor(private element: ElementRef) {}

  @HostListener('scroll')
  onscroll() {}
}
