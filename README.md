# AI Resume Analyser

Production-ready AI Resume Analyser built with Angular 17, Spring Boot 3, PostgreSQL, and OpenAI GPT-4o-mini.

## Features
- Upload PDF/DOCX resumes
- AI-powered ATS scoring (0–100)
- Strengths, Weaknesses, Suggestions
- Keyword optimization vs job description
- Analysis history with pagination

## Prerequisites
- Java 17+
- Node.js 20+
- PostgreSQL 16+
- OpenAI API Key

---

## Quick Start (Local)

### 1. Backend
```bash
cd Backend/AIResumeAnalyser

# Set environment variables
export OPENAI_API_KEY=sk-...
export DB_HOST=localhost
export DB_USER=postgres
export DB_PASSWORD=postgres

mvn spring-boot:run
# Runs on http://localhost:8080
```

### 2. Frontend
```bash
cd Frontend/ai-resume-analyser
npm install
npm start
# Runs on http://localhost:4200 (proxies /api → :8080)
```

---

## Docker Compose (Full Stack)

```bash
# 1. Copy and fill env file
cp .env.example .env

# 2. Start all services
docker-compose up --build

# App available at:
# Frontend: http://localhost:4200
# Backend:  http://localhost:8080
# DB:       localhost:5432
```

---

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/resumes/upload` | Upload PDF/DOCX resume |
| `POST` | `/api/v1/analyses` | Run AI analysis |
| `GET`  | `/api/v1/analyses/{id}` | Get single analysis |
| `GET`  | `/api/v1/analyses/history` | Paginated history |
| `GET`  | `/api/v1/analyses/resume/{id}` | Analyses for a resume |

### Upload Resume
```
POST /api/v1/resumes/upload
Content-Type: multipart/form-data

file: <binary>
```

### Analyse Resume
```json
POST /api/v1/analyses
{
  "resumeId": "uuid",
  "jobDescription": "optional job description text"
}
```

---

## Project Structure
```
AIResumeAnalyser/
├── Backend/AIResumeAnalyser/
│   ├── pom.xml
│   └── src/main/java/com/airesume/
│       ├── config/          CorsConfig, OpenAIConfig
│       ├── controller/      ResumeController, AnalysisController
│       ├── service/         ResumeParserService, AIAnalysisService, AnalysisService
│       ├── repository/      ResumeRepository, AnalysisRepository
│       ├── model/           Resume, Analysis
│       ├── dto/             Request/Response DTOs
│       └── exception/       GlobalExceptionHandler + custom exceptions
├── Frontend/ai-resume-analyser/
│   └── src/app/
│       ├── core/            Models, services, interceptors
│       ├── features/        Upload, Analysis, History components
│       └── shared/          Navbar
└── docker-compose.yml
```

## Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `OPENAI_API_KEY` | required | Your OpenAI API key |
| `openai.model` | `gpt-4o-mini` | OpenAI model to use |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_NAME` | `ai_resume_db` | Database name |
| `DB_USER` | `postgres` | DB username |
| `DB_PASSWORD` | `postgres` | DB password |
