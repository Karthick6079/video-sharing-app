import { CanActivateFn } from '@angular/router';
import { LoginService } from '../services/login/login.service';
import { inject } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { map } from 'rxjs';

export const authGuardGuard: CanActivateFn = (route, state) => {
  const loginService: LoginService = inject(LoginService);
  const oidcSecurityService: OidcSecurityService = inject(OidcSecurityService);

  let isUserAuthenticated = undefined;

  oidcSecurityService.isAuthenticated$.subscribe(
    ({ isAuthenticated }) => (isUserAuthenticated = isAuthenticated)
  );

  if (!isUserAuthenticated) {
    loginService.login();
    return false;
  }

  return true;
};
