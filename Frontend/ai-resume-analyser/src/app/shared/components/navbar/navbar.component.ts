import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule, MatIconModule, MatTooltipModule],
  template: `
    <mat-toolbar color="primary" class="navbar">
      <a routerLink="/upload" class="brand">
        <mat-icon>psychology</mat-icon>
        <span>AI Resume Analyser</span>
      </a>
      <span class="spacer"></span>
      <nav class="nav-links">
        <a mat-button routerLink="/upload" routerLinkActive="active-link">
          <mat-icon>upload_file</mat-icon> Upload
        </a>
        <a mat-button routerLink="/history" routerLinkActive="active-link">
          <mat-icon>history</mat-icon> History
        </a>
      </nav>
    </mat-toolbar>
  `,
  styles: [`
    .navbar { position: sticky; top: 0; z-index: 999; box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
    .brand  { display: flex; align-items: center; gap: 8px; text-decoration: none; color: white; font-size: 1.1rem; font-weight: 700; }
    .spacer { flex: 1 1 auto; }
    .nav-links a { color: rgba(255,255,255,0.85); font-size: 0.9rem; }
    .nav-links a mat-icon { margin-right: 4px; font-size: 18px; vertical-align: middle; }
    .active-link { background: rgba(255,255,255,0.15); border-radius: 4px; }
  `]
})
export class NavbarComponent {}
