import { Component, HostListener, Input } from '@angular/core';

@Component({
  selector: 'app-video-player',
  templateUrl: './video-player.component.html',
  styleUrl: './video-player.component.css',
})
export class VideoPlayerComponent {
  @Input()
  url!: string;

  @Input()
  autoplay: boolean = false;

  hideControls = false;
  private hideControlsTimeout: any;

  

  data: any;
  constructor() {}
  ngOnInit(): void {}
  videoPlayerInit(data: any) {
    this.data = data;
    this.data
      .getDefaultMedia()
      .subscriptions.loadedMetadata.subscribe(this.initVdo.bind(this));
    // this.data
    //   .getDefaultMedia()
    //   .subscriptions.ended.subscribe(this.nextVideo.bind(this));
  }

  initVdo() {
    this.data.play();
  }

  onPlay() {
    this.startHideTimer();
  }

  onPause() {
    this.showControls();
    clearTimeout(this.hideControlsTimeout);
  }

  @HostListener('document:mousemove')
  @HostListener('document:touchstart')
  onUserInteraction() {
    this.showControls();
    if (this.isVideoPlaying()) {
      this.startHideTimer();
    }
  }

  private startHideTimer() {
    clearTimeout(this.hideControlsTimeout);
    this.hideControlsTimeout = setTimeout(() => {
      this.hideControls = true;
    }, 2000); // hide after 2 seconds
  }

  private showControls() {
    this.hideControls = false;
  }

  private isVideoPlaying(): boolean {
    const videoElement = document.getElementById('singleVideo') as HTMLVideoElement;
    return videoElement && !videoElement.paused;
  }
  // startPlaylistVdo(item: any, index: number) {
  //   this.activeIndex = index;
  //   this.currentVideo = item;
  // }
}
