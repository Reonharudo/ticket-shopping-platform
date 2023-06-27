import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {RoomplanCartComponent} from './components/roomplan-cart/roomplan-cart.component';
import {ShoppingCartComponent} from './components/shopping-cart/shopping-cart.component';
import {NewsCreateComponent} from './components/news/news-create/news-create.component';
import {RegisterComponent} from './components/register/register.component';
import {NewsOverviewComponent} from './components/news/news-overview/news-overview.component';
import {EventsComponent} from './components/events/events.component';
import {ToastrModule} from 'ngx-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgOptimizedImage} from '@angular/common';
import {InfiniteScrollModule} from 'ngx-infinite-scroll';

import {EditComponent} from './components/edit/edit.component';

import { NewsDetailComponent } from './components/news/news-detail/news-detail.component';
import { TextareaAutoresizeDirective } from './directives/textarea-autoresize.directive';
import { RoomplaneditorComponent } from './components/roomplaneditor/roomplaneditor.component';
import { RoomplanvisualeditorComponent } from './components/roomplaneditor/roomplanvisualeditor/roomplanvisualeditor.component';
import { SeatrowComponent } from './components/roomplaneditor/seatrow/seatrow.component';
import { ContextmenuComponent } from './components/roomplaneditor/seatrow/contextmenu/contextmenu.component';
import { ToolbarComponent } from './components/roomplaneditor/toolbar/toolbar.component';
import { AddrowbtnComponent } from './components/roomplaneditor/seatrow/addrowbtn/addrowbtn.component';
import { SeatComponent } from './components/roomplaneditor/seatrow/seat/seat.component';
import { SectionmanagerComponent } from './components/roomplaneditor/sectionmanager/sectionmanager.component';
import { CreateSectionComponent } from './components/roomplaneditor/sectionmanager/create-section/create-section.component';
import { ManageSectionComponent } from './components/roomplaneditor/sectionmanager/manage-section/manage-section.component';
import { ReservationsComponent } from './components/reservations/reservations.component';
import { CartCheckoutComponent } from './components/cart-checkout/cart-checkout.component';
import { PurchasesComponent } from './components/purchases/purchases.component';
import { PurchaseDetailComponent } from './components/purchase-detail/purchase-detail.component';
import { TicketListItemComponent } from './components/ticket-list-item/ticket-list-item.component';
import { ReservationCheckoutComponent } from './components/reservation-checkout/reservation-checkout.component';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { EditableSectionComponent }
  from './components/roomplaneditor/sectionmanager/manage-section/editable-section/editable-section.component';
import { EventOverviewComponent } from './components/event-overview/event-overview.component';
import { MatPaginatorModule } from '@angular/material/paginator';
import {AdminBlockUnblockComponent} from './components/admin-block-unblock/admin-block-unblock.component';
import { HallplanManagerComponent } from './components/hallplan-manager/hallplan-manager.component';
import { CustomerhallplandisplayComponent } from './components/customerhallplandisplay/customerhallplandisplay.component';
import { ImmutableseatComponent } from './components/roomplaneditor/seatrow/immutableseat/immutableseat.component';
import { TicketPdfPrintComponent } from './components/ticket-pdf-print/ticket-pdf-print.component';
import { EventDetailComponent } from './components/event-detail/event-detail.component';
import { PerformanceTicketSelctionComponent } from './components/performance-ticket-selction/performance-ticket-selction.component';
import { AdminPasswordResetComponent } from './components/admin-password-reset/admin-password-reset.component';
import { PasswordResetComponent } from './components/password-reset/password-reset.component';
import { UserPasswordResetComponent } from './components/user-password-reset/user-password-reset.component';
import { HallplanCreateComponent } from './components/hallplan-create/hallplan-create.component';
import {TicketReversalInvoicePdfPrintComponent}
  from './components/ticket-reversal-invoice-pdf-print/ticket-reversal-invoice-pdf-print.component';
import { AutocompleteComponent } from './components/autocomplete/autocomplete.component';

import { TicketInvoicePdfPrintComponent } from './components/ticket-invoice-pdf-print/ticket-invoice-pdf-print.component';
import { EventChartComponent } from './components/event-chart/event-chart.component';
import { CanvasJSAngularChartsModule } from '@canvasjs/angular-charts';
import { SectionColorLegendComponent } from './components/section-color-legend/section-color-legend.component';
import { TicketValidatorComponent } from './components/ticket-validator/ticket-validator.component';
import { TopEventsComponent } from './components/top-events/top-events.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    RoomplanCartComponent,
    ShoppingCartComponent,
    EventsComponent,
    RegisterComponent,
    NewsCreateComponent,
    NewsOverviewComponent,
    NewsDetailComponent,
    TextareaAutoresizeDirective,
    RoomplaneditorComponent,
    RoomplanvisualeditorComponent,
    SeatrowComponent,
    ContextmenuComponent,
    ToolbarComponent,
    AddrowbtnComponent,
    SeatComponent,
    SectionmanagerComponent,
    CreateSectionComponent,
    ManageSectionComponent,
    ReservationsComponent,
    CartCheckoutComponent,
    PurchasesComponent,
    PurchaseDetailComponent,
    TicketListItemComponent,
    ReservationCheckoutComponent,
    EditComponent,
    EditableSectionComponent,
    AdminBlockUnblockComponent,
    EventOverviewComponent,
    HallplanManagerComponent,
    CustomerhallplandisplayComponent,
    ImmutableseatComponent,
    TicketPdfPrintComponent,
    EventDetailComponent,
    PerformanceTicketSelctionComponent,
    AdminPasswordResetComponent,
    PasswordResetComponent,
    UserPasswordResetComponent,
    HallplanCreateComponent,
    TicketReversalInvoicePdfPrintComponent,
    AutocompleteComponent,
    TicketInvoicePdfPrintComponent,

    EventChartComponent,
    SectionColorLegendComponent,
    TicketValidatorComponent,
    TopEventsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule,
    NgOptimizedImage,
    InfiniteScrollModule,
    BsDatepickerModule,
    MatPaginatorModule,
    InfiniteScrollModule,
    NgbModule,
    CanvasJSAngularChartsModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
