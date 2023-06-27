import { Component, OnInit } from '@angular/core';
import { Hallplan, PersistedHallplan } from 'src/app/dtos/hallplan/hallplan';
import { HallplanService } from 'src/app/services/hallplan/hallplan.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, Subject, debounceTime } from 'rxjs';


@Component({
  selector: 'app-hallplan-manager',
  templateUrl: './hallplan-manager.component.html',
  styleUrls: ['./hallplan-manager.component.scss']
})
export class HallplanManagerComponent implements OnInit {

  hallPlans: PersistedHallplan[] = [];
  searchName = '';
  searchDescription = '';
  private searchSubject: Subject<{ term1: string; term2: string }> = new Subject<{ term1: string; term2: string }>();

  constructor(
    private hallPlanService: HallplanService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.retrieveAllHallplans('', '');
    this.searchSubject.pipe(debounceTime(300)).subscribe((terms) => {
      this.retrieveAllHallplans(terms.term1, terms.term2);
    });
  }

  onSearch(): void {
    const searchTerms = { term1: this.searchName, term2: this.searchDescription };
    this.searchSubject.next(searchTerms);
  }

  retrieveAllHallplans(term1: string, term2: string): void {

    this.hallPlanService.searchHallplans(term1, term2, true).subscribe({
      next: data => {
        this.hallPlans = data;
      },
      error: error => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Connection to server to retrieve all hallplans failed');
      }
    });

  }

  delete(id: number) {
    if(window.confirm('Do you really want to delete?')) {
      console.log(id);
      this.hallPlanService.deleteHallPlan(id).subscribe({
        next: data => {
          //Deleted
          const indexToDelete = this.hallPlans.findIndex((hallplan) => hallplan.id === id);

          // Check if the index is found (-1 indicates not found)
          if (indexToDelete !== -1) {
            // Remove the entry from the array using the splice() method
            this.hallPlans.splice(indexToDelete, 1);
          }
        },
        error: error => {
          const errorMessage = error.status === 0
            ? 'Server not reachable'
            : error.message.message;
          this.notification.error(errorMessage, 'Connection to server to delete hallplan failed');
        }

      });
    }

  }



}
