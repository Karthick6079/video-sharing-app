import { TestBed } from '@angular/core/testing';

import { ShortsServiceService } from './shorts-service.service';

describe('ShortsServiceService', () => {
  let service: ShortsServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ShortsServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
