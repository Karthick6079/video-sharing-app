import { Component, OnInit } from '@angular/core';
import { VideoService } from '../../services/video/video.service';
import { VideoDto } from '../../dto/video-dto';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-featured',
  templateUrl: './featured.component.html',
  styleUrl: './featured.component.css',
})
export class FeaturedComponent implements OnInit {
  topics!: string[];

  videos!: VideoDto[];

  constructor(private videoService: VideoService) {
    this.topics = [
      'Vijaykanth',
      'Movie',
      'Comedy',
      'Tamilnadu',
      'Chennai',
      'India',
      'Action',
      'Thriller',
      'Movie',
      'Comedy',
      'Tamilnadu',
      'Chennai',
      'India',
    ];
  }
  ngOnInit(): void {
    this.videoService.getVideos().subscribe((videos) => {
      this.videos = videos;
      console.log(this.videos);
    });
  }
}
