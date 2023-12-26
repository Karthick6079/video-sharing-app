import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditVideoMetadataComponent } from './edit-video-metadata.component';

describe('EditVideoMetadataComponent', () => {
  let component: EditVideoMetadataComponent;
  let fixture: ComponentFixture<EditVideoMetadataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditVideoMetadataComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditVideoMetadataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
