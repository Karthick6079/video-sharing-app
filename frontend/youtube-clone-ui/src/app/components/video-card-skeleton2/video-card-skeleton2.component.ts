import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-video-card-skeleton2',
  templateUrl: './video-card-skeleton2.component.html',
  styleUrl: './video-card-skeleton2.component.css',
})
export class VideoCardSkeleton2Component {
  @Input()
  myHeight: any;
}
