import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnauthUiComponent } from './unauth-ui.component';

describe('UnauthUiComponent', () => {
  let component: UnauthUiComponent;
  let fixture: ComponentFixture<UnauthUiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UnauthUiComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UnauthUiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
