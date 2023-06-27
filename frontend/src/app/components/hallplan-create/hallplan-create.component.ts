import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HallplanService } from 'src/app/services/hallplan/hallplan.service';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { Hallplan } from 'src/app/dtos/hallplan/hallplan';

@Component({
  selector: 'app-hallplan-create',
  templateUrl: './hallplan-create.component.html',
  styleUrls: ['./hallplan-create.component.scss']
})
export class HallplanCreateComponent implements OnInit {

  hallplan: Hallplan = {
    name: null,
    description: '',
    seatRows: null,
    isTemplate: true
  };

  registerForm: FormGroup;
  submitted = false;
  //Error flag
  error = false;
  errorMessage = '';

  constructor(
    private hallPlanService: HallplanService,
    private formBuilder: FormBuilder,
    public authService: AuthService,
    private notification: ToastrService,
    private router: Router
  ) {
  }


  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      name: ['', Validators.required],
      //description: ['', Validators.required],

    });
  }

  createHallPlan() {
    console.log(this.hallplan.name);

    this.registerForm.controls['name'].setValue(this.hallplan.name);
    if (this.hallplan.name.trim() === '') {
      return;
    }
    //this.registerForm.controls['description'].setValue(this.hallplan.description);
    console.log(this.registerForm.valid);
    if (this.registerForm.valid) {

      const observable = this.hallPlanService.createHallplan(this.hallplan);
      observable.subscribe({
        next: () => {
          this.router.navigate(['/hallplans/manage']);
          this.notification.success(`${this.hallplan.name} has been successfully registered`);
        },
        error: err => {
          console.error(`Error registering user`, err, this.hallplan);
          if (err.status === 409) {
            this.notification.error(`${err.error.detail}`);
          }
        }
      });
    } else {
      this.registerForm.markAllAsTouched();
    }
  }
}
