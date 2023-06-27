import { TestBed } from '@angular/core/testing';

import { AdminRouteGuard } from './admin-route.guard';

describe('RouteGuardService', () => {
  let service: AdminRouteGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminRouteGuard);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
