import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomepageSkeletonComponent } from './homepage-skeleton.component';

describe('HomepageSkeletonComponent', () => {
  let component: HomepageSkeletonComponent;
  let fixture: ComponentFixture<HomepageSkeletonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HomepageSkeletonComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HomepageSkeletonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
