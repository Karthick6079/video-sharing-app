import { Injectable, OnInit } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ShortsServiceService implements OnInit {
  playShortsSubject = new Subject<boolean>();
  constructor() {}
  ngOnInit(): void {
    console.log('Shorts Service Loaded!');
  }

  playShortsVideo(play: boolean) {
    console.log('Hi3333');
    this.playShortsSubject.next(play);
  }
}
