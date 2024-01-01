import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-thumbnail-video-player',
  templateUrl: './thumbnail-video-player.component.html',
  styleUrl: './thumbnail-video-player.component.css',
})
export class ThumbnailVideoPlayerComponent {
  @Input()
  url =
    'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/2adfcab6-dc28-47eb-af60-2e200ff5ce70.mp4';

  @Input()
  height: string = '350';

  @Input()
  width: string = '150';
}
