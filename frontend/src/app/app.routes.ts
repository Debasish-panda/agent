import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/auth.guard';
import { CustomersComponent, DashboardComponent, EscalationsComponent, KnowledgeComponent, LoginComponent, SimulatorComponent, TicketsComponent } from './pages/pages.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: '', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'simulator', component: SimulatorComponent, canActivate: [authGuard] },
  { path: 'knowledge', component: KnowledgeComponent, canActivate: [authGuard] },
  { path: 'customers', component: CustomersComponent, canActivate: [authGuard] },
  { path: 'tickets', component: TicketsComponent, canActivate: [authGuard] },
  { path: 'escalations', component: EscalationsComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
