export interface ResumeUploadResponse {
  id: string;
  originalFilename: string;
  fileType: string;
  extractedCharCount: number;
  createdAt: string;
}

export interface AnalysisRequest {
  resumeId: string;
  jobDescription?: string;
}

export interface AnalysisResponse {
  id: string;
  resumeId: string;
  originalFilename: string;
  atsScore: number;
  strengths: string[];
  weaknesses: string[];
  suggestions: string[];
  keywords: string[];
  summary: string;
  jobDescription?: string;
  createdAt: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ApiError {
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp: string;
  fieldErrors?: Record<string, string>;
}
