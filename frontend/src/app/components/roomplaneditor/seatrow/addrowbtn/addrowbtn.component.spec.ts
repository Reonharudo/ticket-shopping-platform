import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddrowbtnComponent } from './addrowbtn.component';

describe('AddrowbtnComponent', () => {
  let component: AddrowbtnComponent;
  let fixture: ComponentFixture<AddrowbtnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddrowbtnComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddrowbtnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
