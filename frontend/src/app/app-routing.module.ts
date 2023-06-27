import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { ShoppingCartComponent } from './components/shopping-cart/shopping-cart.component';
import { EventsComponent } from './components/events/events.component';
import { RegisterComponent } from './components/register/register.component';
import { NewsCreateComponent } from './components/news/news-create/news-create.component';
import { NewsOverviewComponent } from './components/news/news-overview/news-overview.component';
import { AdminRouteGuard } from './guards/admin-route.guard';
import { EditComponent } from './components/edit/edit.component';
import { NewsDetailComponent } from './components/news/news-detail/news-detail.component';
import { AdminBlockUnblockComponent } from './components/admin-block-unblock/admin-block-unblock.component';
import { RoomplaneditorComponent } from './components/roomplaneditor/roomplaneditor.component';
import { ReservationsComponent } from './components/reservations/reservations.component';
import { CartCheckoutComponent } from './components/cart-checkout/cart-checkout.component';
import { PurchasesComponent } from './components/purchases/purchases.component';
import { PurchaseDetailComponent } from './components/purchase-detail/purchase-detail.component';
import { ReservationCheckoutComponent } from './components/reservation-checkout/reservation-checkout.component';
import { EventOverviewComponent } from './components/event-overview/event-overview.component';
import { HallplanManagerComponent } from './components/hallplan-manager/hallplan-manager.component';
import { NewsResolver } from './components/news/news.resolver';
import { EventDetailComponent } from './components/event-detail/event-detail.component';
import { AdminPasswordResetComponent } from './components/admin-password-reset/admin-password-reset.component';
import { PasswordResetComponent } from './components/password-reset/password-reset.component';
import { UserPasswordResetComponent } from './components/user-password-reset/user-password-reset.component';
import { PerformanceTicketSelctionComponent } from './components/performance-ticket-selction/performance-ticket-selction.component';
import { HallplanCreateComponent } from './components/hallplan-create/hallplan-create.component';
import { EventChartComponent } from './components/event-chart/event-chart.component';
import { TicketValidatorComponent } from './components/ticket-validator/ticket-validator.component';
import {TopEventsComponent} from './components/top-events/top-events.component';


const routes: Routes = [
  { path: '', component: HomeComponent },

  { path: 'events', canActivate: [AuthGuard], component: EventOverviewComponent },
  { path: 'events/:id', canActivate: [AuthGuard], component: EventDetailComponent },
  { path: 'createevent', canActivate: [AdminRouteGuard], component: EventsComponent },
  { path: 'events-overview', canActivate: [AuthGuard], component: EventOverviewComponent },
  { path: 'events/create', canActivate: [AdminRouteGuard], component: EventsComponent },
  { path: 'ticket-validator/validate', canActivate: [AuthGuard], component: TicketValidatorComponent },
  { path: 'topEvents', canActivate: [AuthGuard], component: TopEventsComponent },

  { path: 'cart', canActivate: [AuthGuard], component: ShoppingCartComponent },
  { path: 'cart/checkout', canActivate: [AuthGuard], component: CartCheckoutComponent },

  {
    path: 'admin', canActivate: [AdminRouteGuard], children: [
      { path: 'block', canActivate: [AdminRouteGuard], component: AdminBlockUnblockComponent },
      { path: 'register', canActivate: [AdminRouteGuard], component: RegisterComponent },
      { path: 'password-reset', canActivate: [AdminRouteGuard], component: AdminPasswordResetComponent }
    ]
  },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'edit', canActivate: [AuthGuard], component: EditComponent },
  { path: 'reset-password', component: PasswordResetComponent },
  { path: 'send-reset-mail', component: UserPasswordResetComponent },
  { path: 'reservations', canActivate: [AuthGuard], component: ReservationsComponent },
  { path: 'reservations/:id/checkout', canActivate: [AuthGuard], component: ReservationCheckoutComponent },

  { path: 'purchases', canActivate: [AuthGuard], component: PurchasesComponent },
  { path: 'purchases/:id', canActivate: [AuthGuard], component: PurchaseDetailComponent },
  { path: 'performance-tickets/:id', canActivate: [AuthGuard], component: PerformanceTicketSelctionComponent },

  { path: 'roomplan/:id/edit', canActivate: [AdminRouteGuard], component: RoomplaneditorComponent },
  { path: 'hallplans/:id/edit', canActivate: [AdminRouteGuard], component: RoomplaneditorComponent },
  { path: 'hallplans/create', component: HallplanCreateComponent },
  { path: 'hallplans/manage', canActivate: [AdminRouteGuard], component: HallplanManagerComponent },

  { path: 'newscreate', canActivate: [AdminRouteGuard], component: NewsCreateComponent },
  {
    path: 'news', canActivate: [AuthGuard], children: [
      { path: '', component: NewsOverviewComponent },
      {
        path: ':id/info', component: NewsDetailComponent, resolve: {
          news: NewsResolver
        }
      }
    ]
  },

  { path: '**', redirectTo: 'news' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true, scrollPositionRestoration: 'enabled' })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
