import { Component, Input, OnInit } from '@angular/core';
import { HallplanService } from '../../services/hallplan/hallplan.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { DetailedPersistedSection } from '../../dtos/hallplan/section';

@Component({
  selector: 'app-section-color-legend',
  templateUrl: './section-color-legend.component.html',
  styleUrls: ['./section-color-legend.component.scss']
})
export class SectionColorLegendComponent implements OnInit {
  @Input() hallplanId: number;

  sections: DetailedPersistedSection[] = [];

  constructor(
    private service: HallplanService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.initByHallplanId();
  }

  fetchAllSections(hallplanId: number) {
    this.service.getAllSectionsWithCounts(hallplanId).subscribe({
      next: data => {
        // filter out unused sections:
        this.sections = data.filter(sec => sec.count !== 0 && sec.name !== 'Unassigned');
      },
      error: error => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Requested Hallplan does not exist');
      }
    });
  }

  initByHallplanId() {
    this.route.paramMap.subscribe(params => {
      const hallplanId = params.get('id');
      if (Number(hallplanId)) {
        this.fetchAllSections(Number(hallplanId));
      } else {
        this.router.navigate(['/events']);
        this.notification.error('Could not fetch hallplan', 'Hallplan with id "' + hallplanId + '" does not exist');
      }
    });
  }

  formatPrice(price: number) {
    return price.toFixed(2).replace('.', ',');
  }
}
