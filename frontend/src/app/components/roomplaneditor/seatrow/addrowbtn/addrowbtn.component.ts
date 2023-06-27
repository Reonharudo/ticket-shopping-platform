import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-addrowbtn',
  templateUrl: './addrowbtn.component.html',
  styleUrls: ['./addrowbtn.component.scss']
})
export class AddrowbtnComponent {
  @Input() onClick: () => any;
}
