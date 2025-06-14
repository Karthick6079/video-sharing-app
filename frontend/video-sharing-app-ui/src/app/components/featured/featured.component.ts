import {
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { VideoService } from '../../services/video/video.service';
import { VideoDto } from '../../dto/video-dto';
import { Observable } from 'rxjs';
import { DOCUMENT } from '@angular/common';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-featured',
  templateUrl: './featured.component.html',
  styleUrl: './featured.component.css',
})
export class FeaturedComponent implements OnInit {
  page: number = 0;
  private SIZE: number = 6;

  topics!: string[];

  videos: VideoDto[] = [];

  isVideosAvailable: boolean = false;

  showLeftAngleButton = false;

  isMobileScreen = false;

  //By default fetch all
  selectedTopic = 'getAll';

  @ViewChild('scrollContent')
  scrollContent: ElementRef;

  constructor(
    private videoService: VideoService,
    private userService: UserService,
    @Inject(DOCUMENT) private document: Document
  ) {
    //Some random default topics
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
    });

    if (window.matchMedia('(max-width: 770px)').matches) {
      this.isMobileScreen = true;
    }
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.checkIfNeedMoreVideos();
    }, 100);
  }
  
  checkIfNeedMoreVideos() {
    if (document.body.scrollHeight <= window.innerHeight) {
      this.getSuggestionVideos();
    }
  }

  moveright() {
    //scroll to right horizontally
    this.showLeftAngleButton = true;
    this.scrollContent.nativeElement.scrollLeft += 75;
  }

  moveleft() {
    const scrollLeftPosition = this.scrollContent.nativeElement.scrollLeft;
    this.scrollContent.nativeElement.scrollLeft -= 75;
    if (scrollLeftPosition == 0) {
      this.showLeftAngleButton = false;
    }
  }

  getSuggestionVideos() {
    this.page = this.page + 1;
    console.log("Page number for suggestion", this.page)
    this.videoService
      .getSuggestedVideos(this.page, this.SIZE)
      .subscribe((videos) => {
        if (videos) {
          this.videos.push(...videos.filter((video) => video.id));
        } else {
          // If video null means no further video in DB. So reseting the page value to 0
          this.page = 0;
        }
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
