import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImmutableseatComponent } from './immutableseat.component';

describe('ImmutableseatComponent', () => {
  let component: ImmutableseatComponent;
  let fixture: ComponentFixture<ImmutableseatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImmutableseatComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImmutableseatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
