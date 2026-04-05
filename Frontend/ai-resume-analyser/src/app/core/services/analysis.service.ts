import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AnalysisRequest,
  AnalysisResponse,
  PagedResponse
} from '../models/analysis.model';

@Injectable({ providedIn: 'root' })
export class AnalysisService {

  private readonly baseUrl = `${environment.apiBaseUrl}/analyses`;

  constructor(private http: HttpClient) {}

  analyse(request: AnalysisRequest): Observable<AnalysisResponse> {
    return this.http.post<AnalysisResponse>(this.baseUrl, request);
  }

  getById(id: string): Observable<AnalysisResponse> {
    return this.http.get<AnalysisResponse>(`${this.baseUrl}/${id}`);
  }

  getHistory(page = 0, size = 10): Observable<PagedResponse<AnalysisResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResponse<AnalysisResponse>>(`${this.baseUrl}/history`, { params });
  }

  getByResume(resumeId: string): Observable<AnalysisResponse[]> {
    return this.http.get<AnalysisResponse[]>(`${this.baseUrl}/resume/${resumeId}`);
  }
}
