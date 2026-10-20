import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from './core/auth.service';
import { ApiService } from './core/api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatIconModule],
  template: `
    @if (router.url.startsWith('/login')) {
      <router-outlet />
    } @else {
      <div class="app-shell">
        <aside class="sidebar">
          <a class="brand" routerLink="/">
            <span class="brand-mark"><mat-icon>graphic_eq</mat-icon></span>
            <span>relay<span class="brand-dot">.</span><small>VOICE PLATFORM</small></span>
          </a>
          <div class="workspace-label">WORKSPACE</div>
          <nav class="nav-list">
            <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}"><span class="material-icons">space_dashboard</span>Overview</a>
            <a routerLink="/simulator" routerLinkActive="active"><span class="material-icons">headset_mic</span>Call simulator<span class="nav-tag">DEMO</span></a>
            <a routerLink="/knowledge" routerLinkActive="active"><span class="material-icons">library_books</span>Knowledge base</a>
            <a routerLink="/customers" routerLinkActive="active"><span class="material-icons">groups</span>Customers</a>
          </nav>
          <div class="workspace-label operations-label">OPERATIONS</div>
          <nav class="nav-list">
            <a routerLink="/tickets" routerLinkActive="active"><span class="material-icons">confirmation_number</span>Support tickets</a>
            <a routerLink="/escalations" routerLinkActive="active"><span class="material-icons">support_agent</span>Escalations</a>
          </nav>
          <div class="sidebar-bottom">
            <div class="status-card"><span class="status-pulse" [class.offline]="!systemOnline"></span><span><b>{{ systemOnline ? 'API connected' : 'API unavailable' }}</b><small>Workspace status</small></span></div>
            <button class="profile" (click)="auth.logout()">
              <span class="avatar">{{ initials }}</span>
              <span class="profile-copy"><b>{{ auth.user()?.email || 'Workspace admin' }}</b><small>Workspace admin</small></span>
              <span class="material-icons">logout</span>
            </button>
          </div>
        </aside>
        <main class="main-area">
          <header class="topbar">
            <div class="breadcrumb"><span>Workspace</span><span class="material-icons">chevron_right</span><b>{{ pageName }}</b></div>
            <div class="topbar-actions"><span class="live-label"><i [class.offline]="!systemOnline"></i>{{ systemOnline ? 'API connected' : 'API offline' }}</span><button class="icon-button" aria-label="Notifications"><span class="material-icons">notifications_none</span></button><span class="avatar top-avatar">{{ initials }}</span></div>
          </header>
          <section class="page-content"><router-outlet /></section>
        </main>
      </div>
    }
  `
})
export class AppComponent implements OnInit {
  readonly auth = inject(AuthService);
  readonly router = inject(Router);
  private readonly api = inject(ApiService);
  systemOnline = false;
  private readonly labels: Record<string, string> = {
    '/': 'Overview', '/simulator': 'Call simulator', '/knowledge': 'Knowledge base',
    '/customers': 'Customers', '/tickets': 'Support tickets', '/escalations': 'Escalations'
  };
  get pageName(): string { return this.labels[this.router.url.split('?')[0]] || 'Overview'; }
  ngOnInit(): void {
    this.api.health().subscribe({ next: () => this.systemOnline = true, error: () => this.systemOnline = false });
    if (this.auth.token) this.auth.loadUser().subscribe({ error: () => this.auth.logout() });
  }
  get initials(): string {
    const email = this.auth.user()?.email ?? 'AD';
    return email.split('@')[0].slice(0, 2).toUpperCase();
  }
}
