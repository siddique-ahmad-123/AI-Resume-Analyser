# ── Stage 1: Build ───────────────────────────────────────────
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build:prod

# ── Stage 2: Serve with Nginx ─────────────────────────────────
FROM nginx:1.25-alpine
COPY --from=build /app/dist/ai-resume-analyser/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
