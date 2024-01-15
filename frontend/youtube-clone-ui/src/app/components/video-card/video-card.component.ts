import { Component, Input, OnInit } from '@angular/core';
import { VideoDto } from '../../dto/video-dto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-video-card',
  templateUrl: './video-card.component.html',
  styleUrl: './video-card.component.css',
})
export class VideoCardComponent implements OnInit {
  constructor(private router: Router) {}

  @Input()
  video!: VideoDto;

  url = '';

  displayVideo = false;

  ngOnInit(): void {
    if (this.video) {
      this.displayVideo = this.isThumbnailAvailable() ? false : true;
    }
  }

  isThumbnailAvailable() {
    return this.video.thumbnailUrl ? true : false;
  }

  displayVideoOnOver(value: boolean) {
    if (!this.isThumbnailAvailable()) {
      this.displayVideo = true;
    } else {
      this.displayVideo = value;
    }
  }

  watchVideo() {
    this.router.navigate(['../../watch', this.video.id]);
  }
}
