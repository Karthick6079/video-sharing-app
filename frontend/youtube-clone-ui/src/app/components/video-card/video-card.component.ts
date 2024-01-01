import { Component } from '@angular/core';

@Component({
  selector: 'app-video-card',
  templateUrl: './video-card.component.html',
  styleUrl: './video-card.component.css',
})
export class VideoCardComponent {
  url =
    'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/2adfcab6-dc28-47eb-af60-2e200ff5ce70.mp4';

  displayVideo = false;
}
