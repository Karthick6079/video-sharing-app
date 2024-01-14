import { Component, Input, OnInit } from '@angular/core';
import { VideoDto } from '../../dto/video-dto';

@Component({
  selector: 'app-suggestion',
  templateUrl: './suggestion.component.html',
  styleUrl: './suggestion.component.css',
})
export class SuggestionComponent implements OnInit {
  @Input()
  video!: VideoDto;

  ngOnInit(): void {
    console.log(this.displayVideo);
  }

  // mouseOnVideo: boolean = false;
  displayVideo: boolean = false;
}
