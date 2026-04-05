import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ResumeService } from '../../core/services/resume.service';
import { AnalysisService } from '../../core/services/analysis.service';
import { ResumeUploadResponse } from '../../core/models/analysis.model';

type UploadStep = 'select' | 'uploaded' | 'analysing';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatSnackBarModule
  ],
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.scss']
})
export class UploadComponent {

  step = signal<UploadStep>('select');
  uploadedResume = signal<ResumeUploadResponse | null>(null);
  selectedFile = signal<File | null>(null);
  isDragging = signal(false);

  form: FormGroup;

  readonly allowedTypes = [
    'application/pdf',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ];

  constructor(
    private fb: FormBuilder,
    private resumeService: ResumeService,
    private analysisService: AnalysisService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      jobDescription: ['']
    });
  }

  // ── Drag & Drop ──────────────────────────────────────────────
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(true);
  }

  onDragLeave(): void {
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
    const file = event.dataTransfer?.files?.[0];
    if (file) this.handleFileSelected(file);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) this.handleFileSelected(file);
  }

  private handleFileSelected(file: File): void {
    if (!this.allowedTypes.includes(file.type)) {
      this.snackBar.open('Only PDF and DOCX files are accepted.', 'Dismiss', { duration: 4000 });
      return;
    }
    if (file.size > 10 * 1024 * 1024) {
      this.snackBar.open('File must be smaller than 10 MB.', 'Dismiss', { duration: 4000 });
      return;
    }
    this.selectedFile.set(file);
    this.uploadFile(file);
  }

  private uploadFile(file: File): void {
    this.step.set('analysing');
    this.resumeService.uploadResume(file).subscribe({
      next: (response) => {
        this.uploadedResume.set(response);
        this.step.set('uploaded');
        this.snackBar.open('Resume uploaded successfully!', '', { duration: 3000 });
      },
      error: () => {
        this.step.set('select');
        this.selectedFile.set(null);
      }
    });
  }

  analyseResume(): void {
    const resume = this.uploadedResume();
    if (!resume) return;

    this.step.set('analysing');
    const jobDescription = this.form.get('jobDescription')?.value?.trim() || undefined;

    this.analysisService.analyse({ resumeId: resume.id, jobDescription }).subscribe({
      next: (result) => {
        this.router.navigate(['/analysis', result.id]);
      },
      error: () => {
        this.step.set('uploaded');
      }
    });
  }

  reset(): void {
    this.step.set('select');
    this.selectedFile.set(null);
    this.uploadedResume.set(null);
    this.form.reset();
  }

  formatBytes(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(1))} ${sizes[i]}`;
  }
}
