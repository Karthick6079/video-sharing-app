import { Component, Input, OnInit } from '@angular/core';
import { VideoDto } from '../../dto/video-dto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-suggestion',
  templateUrl: './suggestion.component.html',
  styleUrl: './suggestion.component.css',
})
export class SuggestionComponent implements OnInit {
  @Input()
  video!: VideoDto;

  @Input()
  showDescription: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {}

  displayVideoOrThumbnail(value: boolean) {
    if (this.video) {
      if (this.video.thumbnailUrl) {
        this.displayVideo = value;
      } else {
        this.displayVideo = true;
      }
    }
  }

  // mouseOnVideo: boolean = false;
  displayVideo: boolean = true;

  watchVideo() {
    this.router.navigate(['../../watch', this.video.videoId]);
  }
}
