import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomplaneditorComponent } from './roomplaneditor.component';

describe('RoomplaneditorComponent', () => {
  let component: RoomplaneditorComponent;
  let fixture: ComponentFixture<RoomplaneditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RoomplaneditorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomplaneditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
