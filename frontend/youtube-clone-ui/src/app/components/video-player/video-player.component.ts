import { Component } from '@angular/core';

@Component({
  selector: 'app-video-player',
  templateUrl: './video-player.component.html',
  styleUrl: './video-player.component.css',
})
export class VideoPlayerComponent {
  videoItems = [
    {
      name: 'Video one',
      src: 'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/a15b4501-ac28-46ae-b255-c2e11efd7c4c.mp4',
      type: 'video/mp4',
    },
    {
      name: 'Video two',
      src: 'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/a15b4501-ac28-46ae-b255-c2e11efd7c4c.mp4',
      type: 'video/mp4',
    },
    {
      name: 'Video three',
      src: 'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/a15b4501-ac28-46ae-b255-c2e11efd7c4c.mp4',
      type: 'video/mp4',
    },
  ];
  activeIndex = 0;
  currentVideo = this.videoItems[this.activeIndex];
  data: any;
  constructor() {}
  ngOnInit(): void {}
  videoPlayerInit(data: any) {
    this.data = data;
    this.data
      .getDefaultMedia()
      .subscriptions.loadedMetadata.subscribe(this.initVdo.bind(this));
    this.data
      .getDefaultMedia()
      .subscriptions.ended.subscribe(this.nextVideo.bind(this));
  }
  nextVideo() {
    this.activeIndex++;
    if (this.activeIndex === this.videoItems.length) {
      this.activeIndex = 0;
    }
    this.currentVideo = this.videoItems[this.activeIndex];
  }
  initVdo() {
    this.data.play();
  }
  startPlaylistVdo(item: any, index: number) {
    this.activeIndex = index;
    this.currentVideo = item;
  }
}
