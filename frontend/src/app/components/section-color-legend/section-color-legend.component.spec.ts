import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectionColorLegendComponent } from './section-color-legend.component';

describe('SectionColorLegendComponent', () => {
  let component: SectionColorLegendComponent;
  let fixture: ComponentFixture<SectionColorLegendComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SectionColorLegendComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SectionColorLegendComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
