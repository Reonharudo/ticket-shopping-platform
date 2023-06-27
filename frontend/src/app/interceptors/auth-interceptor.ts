import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {AuthService} from '../services/auth.service';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private globals: Globals) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authUri = this.globals.backendUri + '/authentication';
    const registerUri = this.globals.backendUri + '/register';
    const resetUri = this.globals.backendUri + '/user/reset-password';
    const resetUriUser = this.globals.backendUri + '/user/send-reset-mail';
    // Do not intercept authentication requests
    if (req.url === authUri) {
      return next.handle(req);
    }

    //Do no intercept register requests
    if (req.url === registerUri) {
      return next.handle(req);
    }

    //Do no intercept reset password requests
    if (req.url === resetUri) {
      return next.handle(req);
    }

    if (req.url === resetUriUser) {
      return next.handle(req);
    }

    const authReq = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + this.authService.getToken())
    });

    return next.handle(authReq);
  }
}
