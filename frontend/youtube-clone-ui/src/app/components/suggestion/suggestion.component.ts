import { Component } from '@angular/core';

@Component({
  selector: 'app-suggestion',
  templateUrl: './suggestion.component.html',
  styleUrl: './suggestion.component.css',
})
export class SuggestionComponent {
  url =
    'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/2adfcab6-dc28-47eb-af60-2e200ff5ce70.mp4';

  mouseOnVideo: boolean = false;
  playThumbnailVideo: boolean = false;

  playVideo() {
    this.playThumbnailVideo = true;
  }
}
