import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ThumbnailVideoPlayerComponent } from './thumbnail-video-player.component';

describe('ThumbnailVideoPlayerComponent', () => {
  let component: ThumbnailVideoPlayerComponent;
  let fixture: ComponentFixture<ThumbnailVideoPlayerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ThumbnailVideoPlayerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ThumbnailVideoPlayerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
