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

  constructor(private router: Router) {
    // this.router.routeReuseStrategy.shouldReuseRoute = function () {
    //   return false;
    // };
  }

  ngOnInit(): void {
    console.log(this.displayVideo);
  }

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
    console.log('The navigate method called from suggestion bar');
    // this.router.navigateByUrl('/watch/' + this.video.id);
    // this.router
    //   .navigateByUrl('/', { skipLocationChange: true })
    //   .then(() => this.router.navigate(['./watch', this.video.id]));
    this.router.navigate(['../../watch', this.video.id]);
  }
}
