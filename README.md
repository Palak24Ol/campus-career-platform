# 🎓 Campus Career & Recruitment Platform

A production-grade microservices system for managing campus placements — built with
Java 17, Spring Boot 3.2, Apache Kafka, Redis, MinIO, WebSocket, Prometheus, and Grafana.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=spring)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-brightgreen?logo=spring)
![Apache Kafka](https://img.shields.io/badge/Kafka-7.5-black?logo=apachekafka)
![Redis](https://img.shields.io/badge/Redis-7-red?logo=redis)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)
![License](https://img.shields.io/badge/license-MIT-green)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running the Platform](#running-the-platform)
- [API Endpoints](#api-endpoints)
- [Kafka Events](#kafka-events)
- [Redis Caching Strategy](#redis-caching-strategy)
- [Monitoring](#monitoring)
- [Core Business Flow](#core-business-flow)
- [Roadmap](#roadmap)

---

## Overview

The Campus Career & Recruitment Platform is a fully event-driven microservices system
designed to digitise and automate the end-to-end placement process at universities.

**Key capabilities:**
- Recruiters register companies, post jobs with eligibility criteria (CGPA, branch, year)
- Students discover eligible jobs, apply with one click, and get real-time push notifications
- Placement cell monitors live analytics: placement %, average package, highest CTC
- All cross-service communication via Apache Kafka — zero tight coupling between services
- Resume storage on MinIO (S3-compatible) — production switch to AWS S3 is one config line

**What makes this architecture interview-ready:**
- Bounded contexts enforced: Application Service owns status transitions, Interview Service
  owns the interview lifecycle — not mixed together
- Admin approval workflow inside Company Service — not a separate Admin microservice
  (eliminates an unnecessary network hop with no benefit)
- Eligibility Engine inside Job Service — eligibility criteria are job properties, not
  a separate domain
- Analytics Service is a pure Kafka consumer — zero HTTP calls to other services,
  zero coupling, pure event sourcing pattern
- Redis used meaningfully: job feed cache, analytics cache, refresh token store,
  rate limiting — not just `@Cacheable` everywhere

---

## Architecture

```
                        ┌─────────────────────────────────┐
                        │        Client (Browser)          │
                        └──────────────┬──────────────────┘
                                       │ HTTP / WebSocket
                        ┌──────────────▼──────────────────┐
                        │         API Gateway :8080        │
                        │  JWT Validation · Rate Limiting  │
                        │  Redis-backed · Eureka-routed    │
                        └──────────────┬──────────────────┘
                                       │ Routes via Eureka
          ┌────────────────────────────┼────────────────────────────┐
          │                            │                            │
  ┌───────▼──────┐           ┌─────────▼──────┐           ┌────────▼──────┐
  │ Auth Service │           │Student Service │           │Company Service│
  │    :8081     │           │    :8082       │           │    :8083      │
  │ JWT · Redis  │           │ MinIO · PG     │           │ Admin APIs    │
  └──────────────┘           └────────────────┘           └───────────────┘
          │                            │                            │
  ┌───────▼──────┐           ┌─────────▼──────┐           ┌────────▼──────┐
  │  Job Service │           │App. Service    │           │ Interview Svc │
  │    :8084     │           │    :8085       │           │    :8086      │
  │ Eligibility  │◄──HTTP────│ Eligibility    │           │ Lifecycle     │
  │ Engine·Redis │           │ Gate·Kafka     │           │ Kafka Events  │
  └──────┬───────┘           └────────┬───────┘           └───────┬───────┘
         │                            │                            │
         └──────────────┬─────────────┘                           │
                        │         Kafka Topics                     │
              ┌─────────▼─────────────────────────────────────────▼──┐
              │  job-events · application-events · interview-events   │
              └─────────────────────┬────────────────────────────────┘
                                    │ Consumes
              ┌─────────────────────┴────────────────────────────────┐
              │                                                        │
   ┌──────────▼──────────┐                              ┌─────────────▼───┐
   │ Notification Service│                              │Analytics Service│
   │       :8087         │                              │     :8088       │
   │ Email · WebSocket   │                              │ Pure Consumer   │
   │ STOMP · Kafka       │                              │ Redis Cache     │
   └─────────────────────┘                              └─────────────────┘

   Infrastructure: Eureka :8761 · PostgreSQL :5432 · Redis :6379
                   Kafka :9092 · MinIO :9000/:9001 · Zookeeper :2181
   Monitoring:     Prometheus :9090 · Grafana :3000
```

---

## Services

| Service | Port | Responsibilities | Database |
|---|---|---|---|
| **Auth Service** | 8081 | JWT login, register, refresh, logout | `auth_db` |
| **Student Service** | 8082 | Profile CRUD, resume upload to MinIO | `student_db` |
| **Company Service** | 8083 | Company CRUD, Admin approval workflow | `company_db` |
| **Job Service** | 8084 | Job CRUD, Eligibility Engine, Redis cache | `job_db` |
| **Application Service** | 8085 | Apply, withdraw, shortlist, offer — with eligibility gate | `application_db` |
| **Interview Service** | 8086 | Schedule, reschedule, cancel interviews | `interview_db` |
| **Notification Service** | 8087 | Email + WebSocket (STOMP) real-time push | `notification_db` |
| **Analytics Service** | 8088 | Placement dashboard — pure Kafka consumer | `analytics_db` |
| **Eureka Server** | 8761 | Service discovery and registration | — |
| **API Gateway** | 8080 | Single entry point, JWT filter, rate limiting | — |

---

## Tech Stack

| Category | Technology |
|---|---|
| Language & Framework | Java 17, Spring Boot 3.2.4, Spring Cloud 2023.0.1 |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway (WebFlux-based) |
| Messaging | Apache Kafka + Zookeeper |
| Caching | Redis 7 |
| Database | PostgreSQL 15 (separate database per service) |
| Database Migration | Flyway |
| Object Storage | MinIO (S3-compatible; AWS SDK with endpoint override) |
| Real-time | Spring WebSocket + STOMP protocol |
| Security | Spring Security + JWT (JJWT 0.12.x) |
| Fault Tolerance | Resilience4j (circuit breaker, retry) |
| Monitoring | Prometheus + Grafana |
| Distributed Tracing | Micrometer + Zipkin |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Containerisation | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Build Tool | Maven (multi-module) |

---

## Project Structure

```
campus-career-platform/
├── pom.xml                          ← Parent POM (root aggregator)
├── docker-compose.yml               ← All services + infrastructure
├── docker-compose.monitoring.yml    ← Prometheus + Grafana (separate)
├── .env.example                     ← Environment template (copy to .env)
├── .gitignore
├── README.md
│
├── .github/workflows/
│   └── ci.yml                       ← GitHub Actions CI/CD
│
├── shared/
│   └── shared-events/               ← Plain Java library (no Spring Boot)
│       └── src/main/java/com/campus/events/
│           ├── JobCreatedEvent.java
│           ├── ApplicationSubmittedEvent.java
│           ├── ApplicationWithdrawnEvent.java
│           ├── StudentShortlistedEvent.java
│           ├── InterviewScheduledEvent.java
│           ├── InterviewRescheduledEvent.java
│           ├── InterviewCancelledEvent.java
│           └── OfferReleasedEvent.java
│
├── infrastructure/
│   ├── eureka-server/               ← Service registry (Milestone 1)
│   └── api-gateway/                 ← JWT filter + rate limiting (Milestone 1)
│
├── services/
│   ├── auth-service/                ← Milestone 2
│   ├── student-service/             ← Milestone 3
│   ├── company-service/             ← Milestone 4
│   ├── job-service/                 ← Milestone 5
│   ├── application-service/         ← Milestone 6
│   ├── interview-service/           ← Milestone 7
│   ├── notification-service/        ← Milestone 8
│   └── analytics-service/           ← Milestone 9
│
└── monitoring/
    ├── prometheus/prometheus.yml
    └── grafana/provisioning/
```

---

## Getting Started

### Prerequisites

| Tool | Version | Install |
|---|---|---|
| JDK | 17 (Temurin) | [adoptium.net](https://adoptium.net) |
| Maven | 3.9+ | [maven.apache.org](https://maven.apache.org) |
| Docker Desktop | Latest | [docker.com](https://docker.com) |
| IntelliJ IDEA | Ultimate (recommended) | [jetbrains.com](https://www.jetbrains.com/idea/) |
| Git | Latest | [git-scm.com](https://git-scm.com) |

**Docker Desktop RAM:** Set to minimum 6 GB (Settings → Resources → Memory).
This project runs 13+ containers simultaneously.

### Clone and Configure

```bash
git clone https://github.com/your-username/campus-career-platform.git
cd campus-career-platform

# Create your .env from the template
cp .env.example .env

# Edit .env — at minimum fill in:
#   JWT_SECRET        (generate: openssl rand -base64 32)
#   POSTGRES_PASSWORD
#   MINIO_ROOT_PASSWORD
#   MAIL_USERNAME + MAIL_PASSWORD (Gmail App Password)
nano .env
```

### Build shared-events first

```bash
# From project root — builds only shared-events module
mvn clean install -pl shared/shared-events -DskipTests

# Verify the JAR was installed to local Maven repo
ls ~/.m2/repository/com/campus/shared-events/1.0.0-SNAPSHOT/
```

---

## Running the Platform

### Start infrastructure only (recommended during development)

```bash
# Start: PostgreSQL, Redis, Kafka, Zookeeper, MinIO
docker compose up postgres redis zookeeper kafka minio -d

# Wait ~30 seconds for Kafka to be ready, then verify:
docker compose ps
```

### Start all services

```bash
# Build and start everything (first time: ~5-10 minutes)
docker compose up --build -d

# View all service logs
docker compose logs -f

# View logs for a specific service
docker compose logs -f auth-service
```

### Access points

| Service | URL | Credentials |
|---|---|---|
| Eureka Dashboard | http://localhost:8761 | — |
| API Gateway | http://localhost:8080 | — |
| MinIO Console | http://localhost:9001 | see .env |
| Prometheus | http://localhost:9090 | — |
| Grafana | http://localhost:3000 | admin / see .env |
| Auth Swagger | http://localhost:8081/swagger-ui.html | — |
| Student Swagger | http://localhost:8082/swagger-ui.html | — |
| Job Swagger | http://localhost:8084/swagger-ui.html | — |

### Stop everything

```bash
docker compose down

# Stop and remove volumes (deletes all database data)
docker compose down -v
```

---

## API Endpoints

### Auth Service (`/auth`)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | None | Register as STUDENT, RECRUITER, or ADMIN |
| POST | `/auth/login` | None | Returns `accessToken` + `refreshToken` |
| POST | `/auth/refresh` | None | Exchange refresh token for new access token |
| POST | `/auth/logout` | Bearer | Invalidate refresh token in Redis |

### Student Service (`/students`)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/students/{id}` | STUDENT / RECRUITER | Get student profile |
| PUT | `/students/{id}` | STUDENT (owner only) | Update profile |
| GET | `/students/search` | RECRUITER | Search by skill, branch, CGPA |
| POST | `/students/{id}/resume` | STUDENT | Upload resume to MinIO |
| GET | `/students/{id}/resume` | STUDENT / RECRUITER | Get presigned resume URL |

### Company Service (`/companies`, `/admin`)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/companies` | RECRUITER | Register company (status: PENDING) |
| GET | `/companies` | Any | List approved companies |
| GET | `/companies/{id}` | Any | Get company details |
| PUT | `/companies/{id}` | RECRUITER (owner) | Update company |
| GET | `/admin/companies/pending` | ADMIN | List companies awaiting approval |
| PUT | `/companies/{id}/approve` | ADMIN | Approve company |
| PUT | `/companies/{id}/reject` | ADMIN | Reject with reason |
| GET | `/admin/recruiters/pending` | ADMIN | List unverified recruiters |
| PUT | `/recruiters/{id}/verify` | ADMIN | Verify recruiter |

### Job Service (`/jobs`)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/jobs` | RECRUITER (approved company) | Create job with eligibility criteria |
| GET | `/jobs` | STUDENT / RECRUITER | Paginated job listing (Redis cached) |
| GET | `/jobs/{id}` | STUDENT / RECRUITER | Get job details |
| PUT | `/jobs/{id}` | RECRUITER (owner) | Update job |
| DELETE | `/jobs/{id}` | RECRUITER (owner) | Close/delete job |
| GET | `/jobs/{id}/eligibility-check` | Internal | Check student eligibility |

### Application Service (`/applications`)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/applications` | STUDENT | Apply (triggers eligibility check) |
| GET | `/applications/student/{id}` | STUDENT | My applications |
| GET | `/applications/job/{jobId}` | RECRUITER | Applicants for a job |
| PUT | `/applications/{id}/status` | RECRUITER | Shortlist / Reject / Offer |
| DELETE | `/applications/{id}` | STUDENT | Withdraw application |

### Interview Service (`/interviews`)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/interviews` | RECRUITER | Schedule interview for shortlisted applicant |
| GET | `/interviews/{id}` | RECRUITER / STUDENT | Get interview details |
| GET | `/interviews/application/{id}` | RECRUITER | Interviews for an application |
| GET | `/interviews/student/{id}` | STUDENT | My upcoming interviews |
| PUT | `/interviews/{id}/reschedule` | RECRUITER | Reschedule |
| PUT | `/interviews/{id}/cancel` | RECRUITER | Cancel |
| PUT | `/interviews/{id}/complete` | RECRUITER | Mark completed |

### Analytics Service (`/analytics`)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/analytics/summary` | ADMIN | Overall placement statistics |
| GET | `/analytics/company/{id}` | ADMIN / RECRUITER | Per-company stats |
| GET | `/analytics/trends` | ADMIN | Monthly application/offer trends |

---

## Kafka Events

| Event | Topic | Producer | Consumers |
|---|---|---|---|
| `JobCreatedEvent` | `job-events` | Job Service | Notification, Analytics |
| `ApplicationSubmittedEvent` | `application-events` | Application Service | Notification, Analytics |
| `ApplicationWithdrawnEvent` | `application-events` | Application Service | Notification |
| `StudentShortlistedEvent` | `application-events` | Application Service | Notification |
| `OfferReleasedEvent` | `application-events` | Application Service | Notification, Analytics |
| `InterviewScheduledEvent` | `interview-events` | Interview Service | Notification |
| `InterviewRescheduledEvent` | `interview-events` | Interview Service | Notification |
| `InterviewCancelledEvent` | `interview-events` | Interview Service | Notification |

**Topic configuration:**

| Topic | Partitions | Key | Retention |
|---|---|---|---|
| `job-events` | 3 | `companyId` | 7 days |
| `application-events` | 3 | `studentId` | 7 days |
| `interview-events` | 3 | `applicationId` | 7 days |

---

## Redis Caching Strategy

| Key Pattern | TTL | Eviction Trigger | Service |
|---|---|---|---|
| `refresh_token:{userId}` | 7 days | DELETE on logout | Auth Service |
| `job_feed:{page}:{size}:{filterHash}` | 5 minutes | Job create/update | Job Service |
| `company:{companyId}` | 30 minutes | Company update/approval | Company Service |
| `analytics:summary` | 10 minutes | Kafka event arrival | Analytics Service |
| Rate limiting keys | 1 second | Auto-expiry | API Gateway |

---

## Monitoring

Prometheus scrapes `/actuator/prometheus` from all 8 services every 15 seconds.

```bash
# Start monitoring stack
docker compose -f docker-compose.monitoring.yml up -d

# Prometheus targets: http://localhost:9090/targets
# Grafana dashboards: http://localhost:3000
```

**Recommended Grafana dashboards to import:**
- Spring Boot dashboard: ID `11378` (from grafana.com/dashboards)
- JVM Micrometer: ID `4701`

**Key metrics exposed:**
- HTTP request rate per endpoint
- Response time percentiles (p50, p95, p99)
- JVM heap usage per service
- Kafka consumer lag
- Cache hit/miss ratios
- Active database connections

---

## Core Business Flow

```
Recruiter creates job (minCgpa=7.5, branches=[CSE,IT])
  → Job stored → JobCreatedEvent published to Kafka
  → Notification Service filters: only eligible students get notified
  → Student (CGPA 8.0, CSE) sees notification: "New job: {title} at {company}"

Student applies → POST /applications
  → Application Service calls Job Service: GET /jobs/{id}/eligibility-check
  → Eligible → Application record created → ApplicationSubmittedEvent published
  → Recruiter gets email: "New application received"

Recruiter shortlists → PUT /applications/{id}/status [SHORTLISTED]
  → StudentShortlistedEvent published
  → Student gets email + 🔔 WebSocket push: "You've been shortlisted!"

Recruiter schedules interview → POST /interviews
  → InterviewScheduledEvent published
  → Student gets email + 🔔 WebSocket push: "Interview on {date} via {meetLink}"

Recruiter releases offer → PUT /applications/{id}/status [OFFERED]
  → OfferReleasedEvent published
  → Student gets email + 🔔 WebSocket push: "🎉 Offer from {company} — {ctc} LPA!"
  → Analytics Service updates: totalOffers++, avgPackage recalculated
  → Redis cache evicted: next /analytics/summary shows fresh stats
```

---

## Roadmap

The following items are designed and documented but not yet implemented.
Both are mentioned intentionally during interviews to show forward-thinking design.

**[1] Elasticsearch Job Search**
Currently using PostgreSQL LIKE queries for job search. The Job Service already
emits `JobCreatedEvent`, so a Search Indexer Service can consume it and index into
Elasticsearch without changing the core service.
*Interview line: "The event-driven design lets me plug in Elasticsearch as a
search indexer without touching Job Service. I've already designed the event contract."*

**[2] Kubernetes Migration**
Currently using Eureka for service discovery. The natural cloud-native evolution
is K8s Service Discovery (replacing Eureka), Horizontal Pod Autoscaler for scaling,
and Ingress Controller replacing Spring Cloud Gateway.
*Interview line: "Eureka is the right starting point. Migrating to K8s Service
Discovery is a configuration change, not an architecture change."*

---

## Build from Source

```bash
# Step 1: Build shared library first
mvn clean install -pl shared/shared-events -DskipTests

# Step 2: Build a specific service
mvn clean package -pl services/auth-service -am -DskipTests

# Step 3: Build everything
mvn clean install -DskipTests

# Step 4: Run a service locally (outside Docker)
java -jar services/auth-service/target/auth-service-1.0.0-SNAPSHOT.jar
```

---
 

 
