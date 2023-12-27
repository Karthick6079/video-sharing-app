import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthInterceptor implements HttpInterceptor {
  constructor(private oidcSecurityService: OidcSecurityService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): any {
    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      const clonedReq = req.clone({
        setHeaders: { Authorization: 'Bearer ' + token },
      });
      console.log(token);
      return next.handle(clonedReq);
    });
  }
}
