import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewAndPublishComponent } from './review-and-publish.component';

describe('ReviewAndPublishComponent', () => {
  let component: ReviewAndPublishComponent;
  let fixture: ComponentFixture<ReviewAndPublishComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReviewAndPublishComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ReviewAndPublishComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
