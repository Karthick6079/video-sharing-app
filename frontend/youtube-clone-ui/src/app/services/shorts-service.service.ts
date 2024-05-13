import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ShortsServiceService {
  constructor() {}

  playShortsSubject = new Subject<boolean>();

  playShortsVideo(play: boolean) {
    this.playShortsSubject.next(play);
  }
}
