import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-user-password-reset',
  templateUrl: './user-password-reset.component.html',
  styleUrls: ['./user-password-reset.component.scss']
})
export class UserPasswordResetComponent {

  passwordResetForm: FormGroup;

  constructor(private userService: UserService,
              private formBuilder: FormBuilder,
              public authService: AuthService,
              private notification: ToastrService) {
    this.passwordResetForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (this.passwordResetForm.invalid) {
      return;
    }

    const email = this.passwordResetForm.controls.email.value;

    const observable = this.userService.sendResetMailUser(email);
    observable.subscribe({
        next: () => {
          this.notification.success('An email with instructions to reset your password has been sent to the provided email address.');
        },
        error: err => {
          this.notification.error('An email with instructions to reset your password has been sent to the provided email address.');
        }
      }
    );
  }

}
