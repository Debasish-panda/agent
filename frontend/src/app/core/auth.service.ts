import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { ApiService } from './api.service';
import { User } from './models';

const TOKEN_KEY = 'relay_access_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  readonly user = signal<User | null>(this.readStoredUser());
  readonly isAuthenticated = computed(() => !!this.token);
  get token(): string | null { return localStorage.getItem(TOKEN_KEY); }

  login(email: string, password: string): Observable<{ token: string; user: User }> {
    return this.api.login(email, password).pipe(tap(response => {
      localStorage.setItem(TOKEN_KEY, response.token);
      localStorage.setItem('relay_user', JSON.stringify(response.user));
      this.user.set(response.user);
    }));
  }

  loadUser(): Observable<User> {
    return this.api.me().pipe(tap(user => {
      localStorage.setItem('relay_user', JSON.stringify(user));
      this.user.set(user);
    }));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem('relay_user');
    this.user.set(null);
    void this.router.navigate(['/login']);
  }

  private readStoredUser(): User | null {
    try {
      const value = localStorage.getItem('relay_user');
      return value ? JSON.parse(value) as User : null;
    } catch {
      return null;
    }
  }
}
