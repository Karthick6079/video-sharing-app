import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { catchError, finalize, Observable, switchMap, take, throwError } from 'rxjs';
import { StatusResponse } from '../dto/status-response';
import { AWSUploadException } from '../exceptions/aws-upload.exception';
import { GenericException } from '../exceptions/generic.exception';
import { UiStateService } from '../services/uistate.service';

@Injectable({
  providedIn: 'root',
})
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private oidcSecurityService: OidcSecurityService,
    private uiState: UiStateService
  ) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.oidcSecurityService.getAccessToken().pipe(
      take(1),
      switchMap((accessToken: string) => {
        const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        this.uiState.showAnimation();
        if (accessToken) {
          const cloned = req.clone({
            setHeaders: {
              Authorization: `Bearer ${accessToken}`
            }
          });
          return next.handle(cloned).pipe(catchError(this.handleUploadFileErrorRespone), finalize(() => {
            this.uiState.hideAnimation();
          }));
        }      
        return next.handle(req).pipe(catchError(this.handleUploadFileErrorRespone), finalize (() => {
          this.uiState.hideAnimation();
        }) );
      })
    );
  }

  handleUploadFileErrorRespone(errorResponse: HttpErrorResponse) {
    console.error('HTTP Error Interceptor:', errorResponse);

    const errObject: StatusResponse = errorResponse.error;

    if (errObject) {
      if (errObject.code == "10000") {
        throw new AWSUploadException();
      } else {
        throw new GenericException();
      }
    }
    return throwError(() => errorResponse);
  }
}
