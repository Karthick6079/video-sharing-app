import { Component, Input, OnInit } from '@angular/core';
import { VideoDto } from '../../dto/video-dto';
import { ActivatedRoute, Router } from '@angular/router';
import { IndianFormatViewCount } from '../../pipes/indianformatviewcount.pipe';

@Component({
  selector: 'app-video-card',
  templateUrl: './video-card.component.html',
  styleUrl: './video-card.component.css',
})
export class VideoCardComponent implements OnInit {
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private indianFormatViewCount: IndianFormatViewCount
  ) {}

  @Input()
  video!: VideoDto;

  url = '';

  displayVideo = false;

  avatarError = false;

  ngOnInit(): void {
    if (this.video) {
      this.displayVideo = this.isThumbnailAvailable() ? false : true;
    }
  }

  isThumbnailAvailable() {
    return this.video.thumbnailUrl ? true : false;
  }

  displayVideoOnOver(value: boolean) {
    if (!this.isThumbnailAvailable()) {
      this.displayVideo = true;
    } else {
      this.displayVideo = value;
    }
  }

  watchVideo() {
    const url = `/watch/${this.video.id}`;
    this.router.navigateByUrl(url);
    // this.router.navigate(['../../watch', this.video.id], {relativeTo:this.route});
  }

  onAvatarError(): void {
    this.avatarError = true;
  }

  getUserInitials(name: string): string {
    if (!name) return '?';
    return name
      .split(' ')
      .map(word => word[0])
      .join('')
      .substring(0, 2)
      .toUpperCase();
  }

}
