import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SimpleVerticalAnimationComponent } from './simple-vertical-animation.component';

describe('SimpleVerticalAnimationComponent', () => {
  let component: SimpleVerticalAnimationComponent;
  let fixture: ComponentFixture<SimpleVerticalAnimationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SimpleVerticalAnimationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SimpleVerticalAnimationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
