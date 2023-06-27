import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerhallplandisplayComponent } from './customerhallplandisplay.component';

describe('CustomerhallplandisplayComponent', () => {
  let component: CustomerhallplandisplayComponent;
  let fixture: ComponentFixture<CustomerhallplandisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomerhallplandisplayComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerhallplandisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
