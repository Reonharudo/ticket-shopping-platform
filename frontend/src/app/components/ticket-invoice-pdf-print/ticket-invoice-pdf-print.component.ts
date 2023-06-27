import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import * as htmlToPdfMake from 'html-to-pdfmake';
import * as pdfMake from 'pdfmake/build/pdfmake';
import * as pdfFonts from 'pdfmake/build/vfs_fonts';
import {Purchase} from 'src/app/dtos/purchases';
import {User} from '../../dtos/user';

@Component({
  selector: 'app-ticket-invoice-pdf-print',
  templateUrl: './ticket-invoice-pdf-print.component.html',
  styleUrls: ['./ticket-invoice-pdf-print.component.scss']
})
export class TicketInvoicePdfPrintComponent implements OnInit, AfterViewInit {

  @ViewChild('pdfContent') pdfContent: ElementRef;

  @Input()
  purchase: Purchase;
  @Input()
  user: User;

  fonts = {
    // eslint-disable-next-line
    Roboto: {
      normal: 'https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/fonts/Roboto/Roboto-Regular.ttf',
      bold: 'https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/fonts/Roboto/Roboto-Medium.ttf',
      italics: `${window.location.origin}/assets/OleoScript-Regular.ttf`
    },
    // eslint-disable-next-line
    OleoScript: {
      normal: `${window.location.origin}/assets/OleoScript-Regular.ttf`
    }
  };
  showPdfContent = false;

  ngOnInit() {
  }

  ngAfterViewInit() {
    //this.generateReversalInvoicePdf();
  }

  public generateInvoicePdf() {
    const paymentMethod = this.getPaymentMethod();
    const pdfContent: any = `
  <div #pdfContent>
    <b style="font-size: 36px; color: #23a6d5">Ticketline - Invoice</b>
    <hr>
    <h3>Invoice Information</h3>
    <p>
      <strong>Invoice Number:</strong> ${this.purchase.purchaseNr}
      <br>
      <strong>Invoice Date:</strong> ${this.purchase.purchaseDate}
      <br>
      <strong>Recipient:</strong> ${this.user.firstName} ${this.user.lastName}
      <br>
      <strong>Address:</strong> ${this.purchase.billAddress}
      <br>
      <strong>Area Code:</strong> ${this.purchase.billAreaCode}
      <br>
      <strong>City:</strong> ${this.purchase.billCityName}
    </p>

    <h3 style="margin-bottom: 0px">Purchased Tickets</h3>
    ${this.purchase.ticketList
      .map(
        ticket => `
          <p style="margin-bottom: 5px">
            <strong>TicketNr:</strong> ${ticket.ticketNr} |
            <strong>Seat:</strong> ${ticket.seat.seatNr} |
             <strong>Price:</strong> ${ticket.seat.price.toFixed(2).replace('.', ',')} €
          </p>
        `
      )
      .join('')}
    <h3>Total Price: ${this.sumOfItems(this.purchase)}€<span style="font-size: 14px;">(including 20% Austrian VAT)</span></h3>

    <h3>Payment Details</h3>
   Payment will be conducted using the payment details provided at the time of purchase:
     <p style ="margin-bottom: 5px">
     <strong>Card Holder:</strong> ${this.user.firstName} ${this.user.lastName}
      <br>
     <strong>Credit Card Nr:</strong> ${this.maskCreditCardNumber(this.purchase.creditCardNr.toString())}
      <br>
     <strong>Expiration Date:</strong> ${this.purchase.expiration}
     </p>
    <h3>Legal Statements</h3>
    <p>
      This is an official invoice for the purchase. Payment is due within 7 days from the invoice date.
      <br>
      Any unauthorized reproduction or distribution of this invoice is strictly prohibited.
    </p>
  </div>
  `;

    const documentDefinition = htmlToPdfMake(pdfContent);
    const doc = {
      content: [documentDefinition]
    };

    const vfs = pdfFonts.pdfMake.vfs;
    pdfMake.createPdf(doc, null, this.fonts, vfs).download('invoice.pdf');
  }


  purchasePresent(): boolean {
    return this.purchase ? true : false;
  }

  sumOfItems(purchase: Purchase): string {
    let sum = 0;
    purchase.ticketList.forEach((element) => {
      sum += element.seat.price;
    });
    return sum.toFixed(2).replace('.', ',');

  }

  getPaymentMethod(): string {
    return 'Credit Card';
  }

  maskCreditCardNumber(creditCardNumber: string): string {
    return creditCardNumber.replace(/.(?=.{4})/g, '*');
  }


}
