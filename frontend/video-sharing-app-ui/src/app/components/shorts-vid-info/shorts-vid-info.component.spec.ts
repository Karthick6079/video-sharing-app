import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShortsVidInfoComponent } from './shorts-vid-info.component';

describe('ShortsVidInfoComponent', () => {
  let component: ShortsVidInfoComponent;
  let fixture: ComponentFixture<ShortsVidInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ShortsVidInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShortsVidInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
