import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AnalysisService } from '../../core/services/analysis.service';
import { AnalysisResponse, PagedResponse } from '../../core/models/analysis.model';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatPaginatorModule, MatTableModule, MatChipsModule, MatTooltipModule
  ],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {

  pagedData = signal<PagedResponse<AnalysisResponse> | null>(null);
  loading = signal(true);

  displayedColumns = ['filename', 'atsScore', 'jobDescription', 'createdAt', 'actions'];
  pageSize = 10;
  pageIndex = 0;

  constructor(
    private analysisService: AnalysisService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPage(0, this.pageSize);
  }

  loadPage(page: number, size: number): void {
    this.loading.set(true);
    this.analysisService.getHistory(page, size).subscribe({
      next: (data) => {
        this.pagedData.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadPage(event.pageIndex, event.pageSize);
  }

  viewAnalysis(id: string): void {
    this.router.navigate(['/analysis', id]);
  }

  uploadNew(): void {
    this.router.navigate(['/upload']);
  }

  scoreClass(score: number): string {
    if (score >= 80) return 'excellent';
    if (score >= 60) return 'good';
    if (score >= 40) return 'average';
    return 'poor';
  }
}
