import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectionmanagerComponent } from './sectionmanager.component';

describe('SectionmanagerComponent', () => {
  let component: SectionmanagerComponent;
  let fixture: ComponentFixture<SectionmanagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SectionmanagerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SectionmanagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
