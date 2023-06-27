import { Component, EventEmitter, Output } from '@angular/core';

export enum ToolbarItem {
  detailedView,
  showSeatRowNr,
  showSeatNr
}

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})
export class ToolbarComponent {
  @Output() toolbarClick = new EventEmitter<ToolbarItem>();

  toolbarItemEnum = ToolbarItem;

  handleToolbarItemClick(clickedItem: ToolbarItem) {
    this.toolbarClick.emit(clickedItem);
  }
}
