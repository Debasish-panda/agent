import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  return localStorage.getItem('relay_access_token') ? true : router.createUrlTree(['/login']);
};

export const guestGuard: CanActivateFn = () => {
  const router = inject(Router);
  return localStorage.getItem('relay_access_token') ? router.createUrlTree(['/']) : true;
};
