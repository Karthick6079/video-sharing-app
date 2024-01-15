import { Component, Input, OnInit } from '@angular/core';
import { UserDto, VideoDto } from '../../dto/video-dto';

@Component({
  selector: 'app-channel-info',
  templateUrl: './channel-info.component.html',
  styleUrl: './channel-info.component.css',
})
export class ChannelInfoComponent implements OnInit {
  @Input()
  video: VideoDto | undefined;

  user: UserDto | undefined;

  ngOnInit(): void {
    this.user = this.video?.userDTO;
  }

  likeVideo() {
    throw new Error('Method not implemented.');
  }
  dislikeVideo() {
    throw new Error('Method not implemented.');
  }
  unsubscribe() {
    throw new Error('Method not implemented.');
  }
  subscribe() {
    throw new Error('Method not implemented.');
  }
  ubsubscribe() {
    throw new Error('Method not implemented.');
  }

  subscribed: boolean = false;
}
