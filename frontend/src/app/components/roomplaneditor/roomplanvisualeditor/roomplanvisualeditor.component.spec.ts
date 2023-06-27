import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomplanvisualeditorComponent } from './roomplanvisualeditor.component';

describe('RoomplanvisualeditorComponent', () => {
  let component: RoomplanvisualeditorComponent;
  let fixture: ComponentFixture<RoomplanvisualeditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RoomplanvisualeditorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomplanvisualeditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
