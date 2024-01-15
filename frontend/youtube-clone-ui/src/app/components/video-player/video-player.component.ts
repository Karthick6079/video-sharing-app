import { Component, Input } from '@angular/core';

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
  // startPlaylistVdo(item: any, index: number) {
  //   this.activeIndex = index;
  //   this.currentVideo = item;
  // }
}
