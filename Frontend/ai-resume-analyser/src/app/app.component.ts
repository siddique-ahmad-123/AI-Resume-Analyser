import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { LoadingService } from './core/services/loading.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, MatProgressBarModule, CommonModule],
  template: `
    <app-navbar />
    <mat-progress-bar
      *ngIf="loadingService.loading()"
      mode="indeterminate"
      color="accent"
      class="global-loader"
    />
    <main class="page-container fade-in-up">
      <router-outlet />
    </main>
  `,
  styles: [`
    .global-loader {
      position: fixed;
      top: 64px;
      left: 0;
      right: 0;
      z-index: 1000;
    }
  `]
})
export class AppComponent {
  constructor(readonly loadingService: LoadingService) {}
}
