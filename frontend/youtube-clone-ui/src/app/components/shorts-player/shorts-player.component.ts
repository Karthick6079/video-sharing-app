import { Component, ElementRef, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-shorts-player',
  templateUrl: './shorts-player.component.html',
  styleUrl: './shorts-player.component.css',
})
export class ShortsPlayerComponent implements OnInit {
  constructor(private elementRef: ElementRef) {}
  ngOnInit(): void {}

  shortsControlList = 'play';

  @Input()
  url =
    'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/9558d281-4958-4314-92f5-37efdfc97067.mp4';

  callMethodOnEnd() {
    console.log('Complete video played!!!');
  }
}
