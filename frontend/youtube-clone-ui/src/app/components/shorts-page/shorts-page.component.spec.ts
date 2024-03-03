import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShortsPageComponent } from './shorts-page.component';

describe('ShortsPageComponent', () => {
  let component: ShortsPageComponent;
  let fixture: ComponentFixture<ShortsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ShortsPageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShortsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
