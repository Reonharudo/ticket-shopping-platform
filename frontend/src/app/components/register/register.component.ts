import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UserService} from '../../services/user.service';
import {User} from '../../dtos/user';
import {ToastrService} from 'ngx-toastr';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  user: User = {
    admin: false,
    email: '',
    firstName: '',
    lastName: '',
    birthdate: new Date(),
    address: '',
    areaCode: null,
    cityName: '',
    password: '',
    isLocked: false

  };
  registerForm: FormGroup;
  submitted = false;
  //Error flag
  error = false;
  errorMessage = '';
  passwordVerify: string;
  passwordPattern = new RegExp(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/);

  constructor(private formBuilder: FormBuilder,
              private userService: UserService,
              public authService: AuthService,
              private notification: ToastrService,
              private router: Router) {
  }

  ngOnInit(): void {

    this.registerForm = this.formBuilder.group({
      admin: [''],
      email: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthdate: ['', Validators.required],
      address: ['', Validators.required],
      areaCode: ['', Validators.required],
      cityName: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(this.passwordPattern)]]
    });
  }

  registerUser(): void {
    this.registerForm.controls['admin'].setValue(this.user.admin);
    this.registerForm.controls['email'].setValue(this.user.email);
    this.registerForm.controls['firstName'].setValue(this.user.firstName);
    this.registerForm.controls['lastName'].setValue(this.user.lastName);
    this.registerForm.controls['birthdate'].setValue(this.user.birthdate);
    this.registerForm.controls['address'].setValue(this.user.address);
    this.registerForm.controls['areaCode'].setValue(this.user.areaCode);
    this.registerForm.controls['cityName'].setValue(this.user.cityName);
    this.registerForm.controls['password'].setValue(this.user.password.trim());
    if (this.registerForm.valid && this.user.password === this.passwordVerify) {
      const observable = this.userService.registerUser(this.user);
      observable.subscribe({
        next: () => {
          this.router.navigate(['/login']);
          this.notification.success(`${this.user.email} has been successfully registered`);
        },
        error: err => {
          console.error(`Error registering user`, err, this.user);
          if (err.status === 409) {
            this.notification.error(`${err.error.detail}`);
          }
        }
      });
    } else {
      this.notification.error('Passwords do not match!');
      this.registerForm.markAllAsTouched();
    }
  }
}
