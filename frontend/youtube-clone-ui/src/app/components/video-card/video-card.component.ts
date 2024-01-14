import { Component, Input, OnInit } from '@angular/core';
import { VideoDto } from '../../dto/video-dto';

@Component({
  selector: 'app-video-card',
  templateUrl: './video-card.component.html',
  styleUrl: './video-card.component.css',
})
export class VideoCardComponent implements OnInit {
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
    if (this.isThumbnailAvailable()) {
      this.displayVideo = true;
    }
    this.displayVideo = value;
  }
}
