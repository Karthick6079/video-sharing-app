import { Component, OnInit } from '@angular/core';
import { VideoDto } from '../../dto/video-dto';
import { VideoService } from '../../services/video/video.service';

@Component({
  selector: 'app-shorts-page',
  templateUrl: './shorts-page.component.html',
  styleUrl: './shorts-page.component.css',
})
export class ShortsPageComponent implements OnInit {
  constructor(private videoService: VideoService) {}

  videos!: VideoDto[];
  ngOnInit(): void {
    this.videoService.getShortsVideo().subscribe((videos) => {
      this.videos = videos;
    });
  }
}
