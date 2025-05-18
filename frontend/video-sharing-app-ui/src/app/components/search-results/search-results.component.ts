import { Component, OnInit } from '@angular/core';
import { VideoService } from '../../services/video/video.service';
import { VideoDto } from '../../dto/video-dto';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs';

@Component({
  selector: 'app-search-results',
  templateUrl: './search-results.component.html',
  styleUrl: './search-results.component.css',
})
export class SearchResultsComponent implements OnInit {
  isApiLoadingData = false;

  searchResultVideos: VideoDto[] = [];

  constructor(
    private videoService: VideoService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap
      .pipe(map((params) => params.get('searchText') || ''))
      .subscribe((result) => {
        const searchTextQuery = result;

        const searchText = searchTextQuery ? searchTextQuery : '';
        this.searchVideos(searchText);
      });
  }

  searchVideos(searchText: string) {
    this.isApiLoadingData = true;
    this.videoService
      .getSearchVideos(searchText)
      .subscribe((searchResult: VideoDto[]) => {
        this.searchResultVideos = searchResult;
        this.isApiLoadingData = false;
      });
  }
}
