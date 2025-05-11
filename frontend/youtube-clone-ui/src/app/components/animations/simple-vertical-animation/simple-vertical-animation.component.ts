import { Component, OnInit } from '@angular/core';
import { UiStateService } from '../../../services/uistate.service';

@Component({
  selector: 'app-simple-vertical-animation',
  templateUrl: './simple-vertical-animation.component.html',
  styleUrl: './simple-vertical-animation.component.css'
})
export class SimpleVerticalAnimationComponent implements OnInit{
  show$ = this.uiStateService.showAnimation$;

  constructor(private uiStateService: UiStateService) {}
  
  ngOnInit(): void {
    // throw new Error('Method not implemented.');
  }
}
