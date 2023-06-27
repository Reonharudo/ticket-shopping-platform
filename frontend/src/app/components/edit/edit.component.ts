import {Component, OnInit} from '@angular/core';
import {User} from '../../dtos/user';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';


export enum EditDeleteMode {
  edit,
  delete,
}

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent implements OnInit {
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

  mode: EditDeleteMode = EditDeleteMode.edit;
  editForm: FormGroup;
  submitted = false;
  //Error flag
  error = false;
  errorMessage = '';
  passwordVerify: string;

  constructor(private formBuilder: FormBuilder,
              private userService: UserService,
              public authService: AuthService,
              private notification: ToastrService,
              private router: Router) {

  }

  public get heading(): string {
    switch (this.mode) {
      case EditDeleteMode.edit:
        return 'Edit Account Details';
      case EditDeleteMode.delete:
        return 'Delete Account';
      default:
        return '?';
    }
  }


  ngOnInit(): void {
    this.userService.getUser(this.authService.getToken()).subscribe(user => {
      this.user = user;
      this.user.password = '';
    });
    this.editForm = this.formBuilder.group({
      admin: [''],
      email: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthdate: ['', Validators.required],
      address: ['', Validators.required],
      areaCode: ['', Validators.required],
      cityName: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  editUser() {
    this.editForm.controls['admin'].setValue(this.user.admin);
    this.editForm.controls['email'].setValue(this.user.email);
    this.editForm.controls['firstName'].setValue(this.user.firstName);
    this.editForm.controls['lastName'].setValue(this.user.lastName);
    this.editForm.controls['birthdate'].setValue(this.user.birthdate);
    this.editForm.controls['address'].setValue(this.user.address);
    this.editForm.controls['areaCode'].setValue(this.user.areaCode);
    this.editForm.controls['cityName'].setValue(this.user.cityName);
    this.editForm.controls['password'].setValue(this.user.password.trim());

    if (this.editForm.valid && this.user.password === this.passwordVerify.trim()) {
      let observable: Observable<any>;
      switch (this.mode) {
        case EditDeleteMode.edit:
          observable = this.userService.editUser(this.user, this.authService.getToken());
          break;
        case EditDeleteMode.delete:
          observable = this.userService.delete(this.user.id, this.user.email, this.user.password);
          break;
        default:
          console.error('Unknown Mode');
      }
      observable.subscribe({
        next: data => {
          switch (this.mode) {
            case EditDeleteMode.edit:
              this.router.navigate(['']);
              this.notification.success(`${this.user.email} has been successfully edited`);
              break;
            case EditDeleteMode.delete:
              this.authService.logoutUser();
              this.router.navigate(['']);
              this.notification.success(`${this.user.email} has been successfully deleted`);
              break;
            default:
              console.error('Unknown Mode');
          }

        },
        error: err => {
          console.error(`Error editing user`, err, this.user);
          if (err.status === 409) {
            this.notification.error(`${err.error.detail}`);
          }
        }
      });
    } else {
      this.notification.error(`Passwords do not match!`);
      this.editForm.markAllAsTouched();
    }
  }

  confirmSubmit() {
    if (confirm('Are you sure you want to submit?')) {
      // User confirmed, submit the form
      this.editUser();
    } else {
      // User cancelled, do nothing
    }
  }

  changeMode(): void {
    if (this.mode === 0) {
      this.mode = 1;
    } else {
      this.mode = 0;
    }

    // Reset Password forms when changing to delete mode
    this.user.password = '';
    this.passwordVerify = '';

  }
}
