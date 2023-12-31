import { Component } from '@angular/core';

@Component({
  selector: 'app-watch',
  templateUrl: './watch.component.html',
  styleUrl: './watch.component.css',
})
export class WatchComponent {
  descPanalOpen = false;

  closeDesc() {
    this.descPanalOpen = false;
    console.log('Deactivate method called from closeDesc');
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
  url: string =
    'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/2adfcab6-dc28-47eb-af60-2e200ff5ce70.mp4';

  subsribed: boolean = false;

  onDeactivate(event: Event) {
    console.log('Deactivate method called');
  }
}
