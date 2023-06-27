import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PersistedSection, RESERVED_DEFAULT_SECTION_NAME } from 'src/app/dtos/hallplan/section';

@Component({
  selector: 'app-manage-section',
  templateUrl: './manage-section.component.html',
  styleUrls: ['./manage-section.component.scss']
})
export class ManageSectionComponent {
  @Input() sections: PersistedSection[];

  @Output() deleteSectionEvent = new EventEmitter<PersistedSection['id']>();
  @Output() updateSectionEvent = new EventEmitter<PersistedSection>();

  isDefaultSection(section: PersistedSection) {
    return section.name === RESERVED_DEFAULT_SECTION_NAME;
  }
}
