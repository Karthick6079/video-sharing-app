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
  page: number = 0;
  private SIZE: number = 3;

  topics!: string[];

  videos!: VideoDto[];

  isVideosAvailable: boolean = false;

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
    this.videoService.getSuggestedVideos(this.page, 6).subscribe((videos) => {
      this.videos = videos;
      this.isVideosAvailable = true;
      console.log(this.videos);
    });
  }

  getSuggestionVideos() {
    this.page = this.page + 1;
    console.log('On scroll method called, The page value is ', this.page);
    this.videoService
      .getSuggestedVideos(this.page, this.SIZE)
      .subscribe((videos) => {
        if (videos) {
          this.videos.push(...videos);
        } else {
          // If video null means no further video in DB. So reseting the page value to 0
          this.page = 0;
        }
        console.log(this.videos);
      });
  }
}
