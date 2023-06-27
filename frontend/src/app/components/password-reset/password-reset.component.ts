import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {ToastrService} from 'ngx-toastr';
import {ResetPasswordUser} from '../../dtos/user';
import {Router} from '@angular/router';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent {

  passwordResetForm: FormGroup;
  error: string;
  success: string;
  submitted = false;
  passwordPattern = new RegExp(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/);

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    private notification: ToastrService
  ) {
    this.passwordResetForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      newPassword: ['', [Validators.minLength(8), Validators.pattern(this.passwordPattern)]],
      confirmPassword: ['', Validators.required]
    });
  }

  onSubmit() {
    this.submitted = true;
    const user: ResetPasswordUser = {
      email: this.passwordResetForm.controls.email.value,
      newPassword: this.passwordResetForm.controls.newPassword.value,
      token: this.getTokenFromUrl()
    };
    if (this.passwordResetForm.valid && this.passwordResetForm.value.confirmPassword === this.passwordResetForm.value.newPassword) {
      const observable = this.userService.resetPassword(user);
      observable.subscribe({
          next: () => {
            this.notification.success('Password successfully reset!');
            this.router.navigate(['login']);
          },
          error: () => {
            this.notification.error('Token invalid. Request a new reset mail!');
          }
        }
      );
    } else {
      this.notification.error('Error reseting Password!');
      this.passwordResetForm.markAllAsTouched();
    }

  }

  private getTokenFromUrl(): string {
    const url = window.location.href;
    const tokenIndex = url.indexOf('token=');
    if (tokenIndex !== -1) {
      return url.substring(tokenIndex + 6);
    }
    return '';
  }
}
