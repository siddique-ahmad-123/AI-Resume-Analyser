import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ResumeUploadResponse } from '../models/analysis.model';

@Injectable({ providedIn: 'root' })
export class ResumeService {

  private readonly baseUrl = `${environment.apiBaseUrl}/resumes`;

  constructor(private http: HttpClient) {}

  uploadResume(file: File): Observable<ResumeUploadResponse> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post<ResumeUploadResponse>(`${this.baseUrl}/upload`, formData);
  }
}
