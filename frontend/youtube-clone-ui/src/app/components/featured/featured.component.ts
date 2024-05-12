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

  //By default fetch all
  selectedTopic = 'getAll';

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
    this.isVideosAvailable = false;
    //Get Trending videos
    this.videoService.getTrendingTopics().subscribe((topics) => {
      this.topics = topics;
      // this.isVideosAvailable = true;
    });

    //get Suggesstion videos

    this.videoService.getSuggestedVideos(this.page, 6).subscribe((videos) => {
      this.videos = videos;
      this.isVideosAvailable = true;
      console.log(this.videos);
    });
  }

  moveright() {
    window.scrollBy(50, 0);
  }

  moveleft() {
    window.scrollBy(-50, 0);
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

  getVideosByTopic(topic: string) {
    if (topic === 'getAll') {
      this.page = 0;
      this.selectedTopic = 'getAll';
      this.getSuggestionVideos();
      return;
    }

    this.selectedTopic = topic;

    this.videoService.getVideosByTopic(topic).subscribe((videos) => {
      if (videos) {
        this.videos = videos;
      } else {
        this.videos = [];
      }
    });
  }
}
