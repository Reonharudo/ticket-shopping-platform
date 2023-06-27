import {Component, OnInit} from '@angular/core';
import {Event} from '../../dtos/event';
import {EventService} from '../../services/event.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';

@Component({
  selector: 'app-event-chart',
  templateUrl: './event-chart.component.html',
  styleUrls: ['./event-chart.component.scss']
})
export class EventChartComponent implements OnInit{
  chart: any;
  goToEvent= (e) =>  {
    this.router.navigateByUrl('/events/'+e.dataPoint.id);
  };
  // eslint-disable-next-line @typescript-eslint/member-ordering
  chartOptions = {
    title: {
      fontFamily: '"Segoe UI",Roboto',
      text: 'Top 10 Events'
    },
    data: [{
      click: this.goToEvent,
      type: 'column',
      dataPoints: [

      ]
    }]
  };
  // eslint-disable-next-line @typescript-eslint/member-ordering
  constructor(
    private eventService: EventService,
    private notification: ToastrService,
    private router: Router
  ) { }
  ngOnInit(): void {
  this.fetchTopEvents();
  }
  getChartInstance(chart: object) {
    this.chart = chart;
  }

  fetchTopEvents(): Promise<Event []> {
    return new Promise((resolve, reject) => {
      this.eventService.getTopEvents().subscribe({
        next: data => {
          console.log(data);
          let count = 0;
          for (const event of data) {
            if(event.soldTickets > 0){
              this.addDataPoint(event.title, count, event.soldTickets, event.id);
              count++;
            }
          }
          this.chart.render();
          resolve(data);
        },
        error: error => {
          console.error('Error fetching events', error);
          this.notification.error(`Could not fetch events. Errorcode: ${error.status}, Errortext: ${error.error.errors}`);
          reject(error);
        }
      });
    });
  }

  addDataPoint(label: string, count: number, value: number, id: number): void {
    const newDataPoint = { label,x: count, y: value, id };
    this.chartOptions.data[0].dataPoints.push(newDataPoint);
  }


}
