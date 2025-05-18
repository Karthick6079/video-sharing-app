import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoCardSkeleton2Component } from './video-card-skeleton2.component';

describe('VideoCardSkeleton2Component', () => {
  let component: VideoCardSkeleton2Component;
  let fixture: ComponentFixture<VideoCardSkeleton2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VideoCardSkeleton2Component]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(VideoCardSkeleton2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
