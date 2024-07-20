import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { ShortsServiceService } from '../../services/shorts-service.service';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-shorts-player',
  templateUrl: './shorts-player.component.html',
  styleUrl: './shorts-player.component.css',
})
export class ShortsPlayerComponent implements OnInit {
  constructor(
    private elementRef: ElementRef,
    private shortsService: ShortsServiceService,
    private userService: UserService
  ) {}
  ngOnInit(): void {
    this.shortsService.playShortsSubject.subscribe((play) => {
      if (play) {
        this.playVideo();
      }
    });

    this.userService.playShortsOnFocus.subscribe((play) => {
      if (play) {
        this.playVideo();
      }
    });
  }

  shortsControlList = 'play';

  @ViewChild('media') videoElement: ElementRef;

  @Input()
  url =
    'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/9558d281-4958-4314-92f5-37efdfc97067.mp4';

  @Input()
  autoplay = false;

  callMethodOnEnd() {}

  playVideo() {
    this.videoElement.nativeElement.play();
  }
}
