import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { PersistedHallplan } from 'src/app/dtos/hallplan/hallplan';
import { CreateSectionPayload } from './create-section/create-section.component';
import { HallplanService } from 'src/app/services/hallplan/hallplan.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { PersistedSection } from 'src/app/dtos/hallplan/section';

enum SubmenuPage {
  addSection = 'create',
  manageSection = 'manage'
}

@Component({
  selector: 'app-sectionmanager',
  templateUrl: './sectionmanager.component.html',
  styleUrls: ['./sectionmanager.component.scss']
})
export class SectionmanagerComponent implements OnInit, OnChanges {
  @Input() roomplan: PersistedHallplan;
  @Output() createSectionEvent = new EventEmitter<CreateSectionPayload>();

  sections: PersistedSection[] = [];
  sectionpage: SubmenuPage; //curent page
  submenuPageEnum = SubmenuPage; //enum reference

  constructor(
    private service: HallplanService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnChanges(): void {
    this.fetchAllSections(this.roomplan.id);
  }

  ngOnInit(): void {
    this.initQueryParams();
    this.initByHallplanId();
  }

  initQueryParams() {
    this.route.queryParams
      .subscribe(params => {
        const sectionPageParam = params.sectionpage;
        switch (sectionPageParam) {
          case SubmenuPage.addSection:
            this.sectionpage = SubmenuPage.addSection;
            break;
          case SubmenuPage.manageSection:
            this.sectionpage = SubmenuPage.manageSection;
            break;
          default:
            this.sectionpage = SubmenuPage.addSection;
        }
      }
      );
  }


  persistUpdatedSection(section: PersistedSection) {
    this.service.updateSection(this.roomplan.id, section.id, section).subscribe(
      {
        next: () => {
          this.fetchAllSections(Number(this.roomplan.id));
        },
        error: (error) => {
          const errorMessage = error.status === 0
            ? 'Server not reachable'
            : error.message.message;
          this.notification.error(errorMessage, 'Could not update section ' + error);
        }
      }
    );
  }

  initByHallplanId() {
    this.route.paramMap.subscribe(params => {
      const hallplanId = params.get('id');
      if (Number(hallplanId)) {
        this.fetchAllSections(Number(hallplanId));
      } else {
        this.router.navigate(['/hallplans']);
      }
    });
  }

  handleDeleteSection(sectionId: number) {
    const deletionIndex = this.sections.findIndex(section => section.id === sectionId);

    //persist deletion
    this.service.deleteSection(sectionId).subscribe({
      next: () => {
        if (deletionIndex >= 0) {
          this.sections.splice(deletionIndex, 1);
        }
      },
      error: error => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Selection has assigned seats. Delete associated seats first or re-assign.');
      }
    });
  }

  fetchAllSections(hallplanId: number) {
    this.service.getAllSections(hallplanId).subscribe({
      next: data => {
        this.sections = data;
      },
      error: error => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Requested Hallplan does not exist');
      }
    });
  }


  switchPage(navigateTo: SubmenuPage) {
    this.router.navigate(
      [],
      {
        relativeTo: this.route,
        queryParams: {
          sectionpage: navigateTo
        },
        queryParamsHandling: 'merge', // remove to replace all query params by provided
      });
  }
}
