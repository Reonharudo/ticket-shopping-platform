import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { DetailedPersistedSection, PersistedSection, RESERVED_DEFAULT_SECTION_NAME } from 'src/app/dtos/hallplan/section';
import { HallplanService } from 'src/app/services/hallplan/hallplan.service';

@Component({
  selector: 'app-editable-section',
  templateUrl: './editable-section.component.html',
  styleUrls: ['./editable-section.component.scss']
})
export class EditableSectionComponent {
  @Input() section: DetailedPersistedSection;
  @Output() deleteSectionEvent = new EventEmitter<PersistedSection['id']>();
  @Output() updateSectionEvent = new EventEmitter<PersistedSection>();

  isInEditMode = false;

  name = '';
  price = 0;

  priceErrMessage = '';
  nameErrMessage = '';

  isDefaultSection(section: PersistedSection) {
    return section.name === RESERVED_DEFAULT_SECTION_NAME;
  }

  handleNameInputChange(updatedName: string) {
    if (updatedName.length === 0) {
      this.nameErrMessage = 'Name is mandatory';
    } else {
      this.nameErrMessage = '';
    }
    this.name = updatedName;
  }

  handlePriceChange(newPriceInput: string) {
    if (newPriceInput.endsWith('.')) {
      newPriceInput += '0';
    }

    if (this.isValidCurrencyNumber(newPriceInput)) {
      const newPrice = Number(newPriceInput);

      //update state
      this.price = newPrice;

      //validate price
      if (newPrice > 0) {
        this.priceErrMessage = '';
      } else if (newPrice <= 0) {
        this.priceErrMessage = 'Price must be larger than zero';
      }
    } else if (newPriceInput.length === 0) {
      this.priceErrMessage = 'Price is mandatory';
    } else {
      this.priceErrMessage = 'Invalid price, may only contain up to two decimal points or none';
    }
  }

  persistNewPrice() {
    let updatedPrice = this.price;
    if (updatedPrice === 0) {
      updatedPrice = this.section.price;
    }
    if (this.priceErrMessage.length === 0) {
      this.updateSectionEvent.emit({
        ...this.section,
        price: updatedPrice
      });
    }
  }

  /**
   *   // Check if the input is a valid number with optional two decimal places
   *
   * @param input that may resemble a number
   * @returns if given input is valid currency number
   */
  isValidCurrencyNumber(input: string) {
    return /^\d+(\.\d{1,2})?$/.test(input);
  }
}
