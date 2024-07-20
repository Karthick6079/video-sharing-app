import { Injectable, OnInit } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ShortsServiceService implements OnInit {
  playShortsSubject = new Subject<boolean>();
  constructor() {}
  ngOnInit(): void {}

  playShortsVideo(play: boolean) {
    this.playShortsSubject.next(play);
  }
}
