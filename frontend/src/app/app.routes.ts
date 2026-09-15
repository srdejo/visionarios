import { Routes } from '@angular/router';
import { Component } from '@angular/core';
import { authGuard, adminGuard, rootRedirectGuard } from './core/guards';

@Component({ selector: 'app-root-redirect', template: '' })
class RootRedirect {}

export const routes: Routes = [
  { path: '', pathMatch: 'full', canActivate: [rootRedirectGuard], component: RootRedirect },
  {
    path: 'login',
    loadComponent: () => import('./pages/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'registro',
    loadComponent: () => import('./pages/auth/register/register').then((m) => m.Register),
  },
  {
    path: 'olvide-password',
    loadComponent: () => import('./pages/auth/forgot-password/forgot-password').then((m) => m.ForgotPassword),
  },
  {
    path: 'aceptar-invitacion/:token',
    loadComponent: () => import('./pages/accept-invite/accept-invite').then((m) => m.AcceptInvite),
  },
  {
    path: 'restablecer/:token',
    loadComponent: () => import('./pages/auth/reset-password/reset-password').then((m) => m.ResetPassword),
  },
  {
    path: 'verificar-correo/:token',
    loadComponent: () => import('./pages/auth/verify-email/verify-email').then((m) => m.VerifyEmail),
  },
  {
    path: 'darme-de-baja/:token',
    loadComponent: () => import('./pages/unsubscribe/unsubscribe').then((m) => m.Unsubscribe),
  },
  {
    path: 'home',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/home/home').then((m) => m.Home),
  },
  {
    path: 'biblioteca',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/biblioteca/biblioteca').then((m) => m.Biblioteca),
  },
  {
    path: 'agenda',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/agenda/agenda').then((m) => m.Agenda),
  },
  {
    path: 'eventos/:id',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/evento-detalle/evento-detalle').then((m) => m.EventoDetalle),
  },
  {
    path: 'perfil',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/perfil/perfil').then((m) => m.Perfil),
  },
  {
    path: 'admin/eventos',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./pages/admin/eventos/admin-eventos').then((m) => m.AdminEventos),
  },
  {
    path: 'admin/material',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./pages/admin/material/admin-material').then((m) => m.AdminMaterial),
  },
  {
    path: 'admin/usuarios',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./pages/admin/usuarios/admin-usuarios').then((m) => m.AdminUsuarios),
  },
  {
    path: 'admin/equipo',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./pages/admin/equipo/admin-equipo').then((m) => m.AdminEquipo),
  },
  { path: '**', redirectTo: 'login' },
];
