import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { videoDataResolverResolver } from './video-data-resolver.resolver';

describe('videoDataResolverResolver', () => {
  const executeResolver: ResolveFn<boolean> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => videoDataResolverResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
