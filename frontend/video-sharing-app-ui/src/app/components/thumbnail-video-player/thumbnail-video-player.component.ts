import { AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-thumbnail-video-player',
  templateUrl: './thumbnail-video-player.component.html',
  styleUrl: './thumbnail-video-player.component.css',
})
export class ThumbnailVideoPlayerComponent implements OnInit, AfterViewInit {
  @Input()
  url =
    'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/2adfcab6-dc28-47eb-af60-2e200ff5ce70.mp4';

  @Input()
  thumbnailImageUrl = '';

  isImageLoadError = false;

  @ViewChild('videoPlayer') videoPlayer!: ElementRef<HTMLVideoElement>;
  // @ViewChild('captureCanvas') captureCanvas!: ElementRef<HTMLCanvasElement>;

  dynamicThumbnailUrl: string | null = null;
  isHovering = false;
  isVideoLoadError = false;

  ngOnInit() {
    if (!this.thumbnailImageUrl) {
      // this.generateThumbnailFromVideo();
    }
  }

  ngAfterViewInit() {
    const videoEl = this.videoPlayer?.nativeElement;
    if (videoEl) {
      videoEl.load(); // Helps preload the video
    }
  }

  playPreview() {
    console.log('Hover triggered');
    this.isHovering = true;
    this.isVideoLoadError = false;
    setTimeout(() => {
      const videoEl = this.videoPlayer?.nativeElement;
      if (videoEl?.readyState >= 2) {
        videoEl.currentTime = 0.1
        videoEl.play().catch(() => {
          this.isVideoLoadError = true;
        });
      }
    }, 50);
  }

  stopPreview() {
    const videoEl = this.videoPlayer?.nativeElement;
    if (videoEl) {
      videoEl.pause();
      videoEl.currentTime = 0;
    }
    this.isHovering = false;
  }

  onVideoError() {
    this.isVideoLoadError = true;
  }

  onImageError(){
    console.log("error method called on image load issue")
    this.isImageLoadError = true;
  }

}
