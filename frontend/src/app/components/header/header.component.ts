import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { NavigationEnd, Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
  isAdminDropdownPageActive = false;
  isAccountDropdownPageActive = false;

  private routerSubscription: Subscription;
  constructor(public authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.routerSubscription = this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        // Get the new path
        const newPath = event.url;

        // Call your method here
        this.handleNewPath(newPath);
      }
    });
  }

  ngOnDestroy() {
    this.routerSubscription.unsubscribe();
  }

  handleNewPath(newPath: string) {
    const adminLinks = [
      '/events/create',
      '/news/create',
      '/hallplans/manage',
      '/admin/block',
      '/admin/register',
      '/admin/password-reset'
    ];
    this.isAdminDropdownPageActive = adminLinks.includes(newPath);

    const accountLinks = [
      '/edit',
      '/reservations',
      '/purchases',
    ];
    this.isAccountDropdownPageActive = accountLinks.includes(newPath);
  }
}
