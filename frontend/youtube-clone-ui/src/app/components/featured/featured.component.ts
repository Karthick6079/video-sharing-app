import { Component } from '@angular/core';

@Component({
  selector: 'app-featured',
  templateUrl: './featured.component.html',
  styleUrl: './featured.component.css',
})
export class FeaturedComponent {
  topics!: string[];

  constructor() {
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
}
