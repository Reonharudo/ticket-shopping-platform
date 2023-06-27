import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {BlockUser, ResetPasswordUser, User} from '../dtos/user';
import {DeleteUser} from '../dtos/user';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {AuthService} from './auth.service';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  private registerUri: string = this.globals.backendUri + '/register';
  private adminBaseUri: string = this.globals.backendUri + '/admin';
  private userBaseUri: string = this.globals.backendUri + '/user';

  constructor(
    public authService: AuthService,
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Register a new user.
   *
   * @param user the user which should be persisted in the database
   * @return an Observable of the user which has been registered
   * */
  registerUser(user: User): Observable<User> {
    if (this.authService.isLoggedIn() && this.authService.getUserRole() === 'ADMIN') {
      return this.http.post<User>(
        this.adminBaseUri, user
      );
    } else {
      return this.http.post<User>(
        this.registerUri, user
      );
    }
  }

  /**
   * Get a user.
   *
   * @return an Observable of the user which was retrieved
   * */
  getUser(token: string): Observable<User> {
    if (this.authService.isLoggedIn()) {
      return this.http.get<User>(
        this.userBaseUri + '?token=' + token
      );
    }
  }

  /**
   * logged-in user fetches itself user.
   *
   * @return an Observable of the user which was retrieved
   * */
  getSelf(): Observable<User> {
    if (this.authService.isLoggedIn()) {
      return this.http.get<User>(
        this.userBaseUri
      );
    }
  }

  /**
   * Edit a user in the database.
   *
   * @param user the values which should be edited
   * @return an Observable of the user which was edited
   * */
  editUser(user: User, token: string): Observable<User> {
    if (this.authService.isLoggedIn()) {
      return this.http.put<User>(
        this.userBaseUri + '?token=' + token, user
      );
    }
  }

  /**
   * Delete a user in the database.
   *
   * @param id the id of the user which will be deleted
   * @param email the email of the user
   * @param password the password of the user
   * */
  delete(id: number, email: string, password: string) {

    const user: DeleteUser = {
      id,
      email,
      password
    };
    return this.http.delete(this.userBaseUri, {body: user});
  }

  /**
   * Block|Unblock a user in the database
   *
   * @param blockUser the user which will be blocked
   * */
  blockUser(blockUser: BlockUser) {
    return this.http.put(this.adminBaseUri, blockUser);
  }

  /**
   * Loads a blocked/unblocked user-page from the backend.
   *
   * @param user the search options with email and isLocked status that should be fetched
   * @param pageIndex index of the page that should fetched
   * @return an Observable for the fetched page of the user entries
   * */
  getBlockedUser(user: BlockUser, pageIndex: number): Observable<BlockUser[]> {
    let params = new HttpParams();
    params = params.append('email', user.email);
    params = params.append('isLocked', user.isLocked);
    params = params.append('pageIndex', pageIndex);
    return this.http.get<BlockUser[]>(this.adminBaseUri, {params});
  }

  /**
   * Block|Unblock a user in the database
   *
   * @param unblockUser the user which will be unblocked
   * */
  unblockUser(unblockUser: BlockUser) {
    return this.http.put(this.adminBaseUri, unblockUser);
  }

  sendResetMail(email: string): Observable<any> {
    const resetUrl = `${this.adminBaseUri}/send-reset-mail`;
    return this.http.post(resetUrl, email);
  }

  sendResetMailUser(email: string): Observable<any> {
    const resetUrl = `${this.userBaseUri}/send-reset-mail`;
    return this.http.post(resetUrl, email);
  }

  resetPassword(user: ResetPasswordUser): Observable<any> {
    const resetPasswordUrl = `${this.userBaseUri}/reset-password`;
    return this.http.post(resetPasswordUrl, user);
  }
}

