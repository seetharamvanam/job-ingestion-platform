# Job Ingestion Platform

Job Ingestion Platform is a Spring Boot service that collects software-related job postings from company career sites, stores them in PostgreSQL, and exposes REST APIs for reading job sources, ingested jobs, and ingestion-run history.

The platform currently supports **Greenhouse** and **Lever**. A provider-based architecture keeps job-board-specific scraping and parsing outside the main ingestion workflow, making it easier to add more providers without rewriting the orchestration layer.

## Features

- Database-backed job-source management
- Scheduled ingestion with configurable fixed delay
- Overlapping-run protection
- Greenhouse listing pagination and job-detail scraping
- Lever listing and job-detail scraping
- Provider resolution through `JobBoardProviderFactory`
- Software-role filtering by title and department
- Duplicate prevention per source and external job ID
- Ingestion-run tracking with found, inserted, and skipped counts
- Paginated job APIs
- Docker Compose setup for the application, PostgreSQL, and pgAdmin
- Unit tests for Lever parsing, detail extraction, and provider orchestration

## Supported Providers

| Provider | Status | Listing pagination | Job-detail scraping |
|---|---|---:|---:|
| Greenhouse | Supported | Yes | Yes |
| Lever | Supported | Not currently required | Yes |
| Ashby | Planned | TBD | Planned |
| Workday | Planned | TBD | Planned |

`WORKDAY` currently exists in `JobBoardProviderType`, but a `WorkdayProvider` has not been implemented. Do not create a Workday source yet; ingestion cannot resolve it through the provider factory.

## How It Works

```text
Job source API
  -> PostgreSQL job_sources table
  -> scheduled ingestion
  -> JobBoardProviderFactory
       -> GreenHouseProvider
       -> LeverProvider
  -> provider-specific listing scrape and parse
  -> software job filter
  -> duplicate check
  -> provider-specific job-detail enrichment
  -> PostgreSQL job_postings table
  -> jobs API
```

Every active source includes a provider type. During ingestion, `JobBoardProviderFactory` selects the matching Spring-managed `JobBoardProvider`. Each provider returns the same `ScrapedJob` model, so filtering, duplicate detection, mapping, and persistence remain provider-independent.

Descriptions are fetched only after a job passes the software-role filter and duplicate check. This avoids unnecessary detail-page requests for irrelevant or previously stored jobs.

## Provider Behavior

### Greenhouse

- Downloads server-rendered Greenhouse pages with Jsoup
- Detects listing pagination and processes every page
- Extracts external ID, title, department, location, and job URL
- Extracts detail text from `div.job__description.body`

### Lever

- Downloads server-rendered Lever pages with Jsoup
- Parses jobs from `div.posting` elements
- Reads the posting UUID from `data-qa-posting-id`, with URL-based fallback
- Extracts titles, locations, and team/department information
- Falls back to the surrounding posting-group title when a team is unavailable
- Extracts detail text from `[data-qa=job-description]`
- Preserves standard and EU Lever job URLs instead of hard-coding a Lever domain

## Tech Stack

- Java 21
- Spring Boot 3.5.0
- Gradle 9.5.1 wrapper
- Spring Web
- Spring Data JPA
- PostgreSQL 16
- Jsoup
- Lombok
- Docker and Docker Compose
- JUnit 5 and Mockito through Spring Boot Test

## Repository Layout

```text
.
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker-compose.yaml
├── gradlew / gradlew.bat
├── gradle/wrapper/
├── src/main/java/com/jobingestion/jobingestionplatform/
│   ├── JobIngestionPlatformApplication.java
│   ├── filter/                 # Software-role filtering
│   ├── ingestion/              # Main ingestion orchestration
│   ├── ingestionrun/           # Run status, history, and APIs
│   ├── job/                    # Job persistence and read APIs
│   ├── provider/
│   │   ├── JobBoardProvider.java
│   │   ├── JobBoardProviderFactory.java
│   │   ├── detail/             # Shared detail-parser contract
│   │   ├── model/              # Provider-independent ScrapedJob
│   │   ├── parser/             # Shared listing-parser contract
│   │   ├── scraper/            # Shared scraper contract
│   │   ├── greenhouse/         # Greenhouse implementation
│   │   └── lever/              # Lever implementation
│   ├── scheduler/              # Fixed-delay scheduler
│   └── source/                 # Job-source persistence and APIs
├── src/main/resources/
│   ├── application.properties
│   └── banner.txt
└── src/test/java/com/jobingestion/jobingestionplatform/
    ├── detail/                 # Greenhouse detail-parser tests
    └── lever/                  # Lever parser and provider tests
```

## Prerequisites

- JDK 21
- Docker Desktop or another Docker Compose-compatible runtime
- A terminal opened at the repository root
- An API client such as curl, Postman, or Insomnia

Gradle does not need to be installed separately. Use the included wrapper:

- Windows: `.\gradlew.bat`
- macOS/Linux: `./gradlew`

## Configuration

For local execution with `bootRun`, create a `.env` file in the repository root:

```properties
DATABASE_NAME=jobIngestion
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password
```

The file is optional to Spring but the referenced database variables are required unless equivalent values are supplied another way. `.env` is ignored by Git and should not be committed.

Relevant application properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/${DATABASE_NAME}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
app.scheduler.ingestion-fixed-delay-ms=120000
```

`ddl-auto=update` is convenient for local development. Use explicit migrations rather than automatic schema updates for a production deployment.

## Run Locally

### Option 1: Run PostgreSQL in Docker and Spring Boot locally

Start PostgreSQL and, optionally, pgAdmin:

```powershell
docker compose up -d db pgadmin
```

Create the `.env` file described above, then start the application.

Windows:

```powershell
.\gradlew.bat bootRun
```

macOS/Linux:

```bash
./gradlew bootRun
```

### Option 2: Run the entire stack with Docker Compose

The Dockerfile copies an already-built JAR, so build the application before building the image.

Windows:

```powershell
.\gradlew.bat clean build
docker compose up --build -d
```

macOS/Linux:

```bash
./gradlew clean build
docker compose up --build -d
```

Services:

| Service | Address |
|---|---|
| Application | `http://localhost:8080` |
| PostgreSQL | `localhost:5432` |
| pgAdmin | `http://localhost:8181` |

Default database credentials:

```text
Database: jobIngestion
Username: postgres
Password: password
```

Default pgAdmin login:

```text
Email: admin@example.com
Password: password
```

Stop the containers without deleting data:

```powershell
docker compose down
```

Stop the containers and delete the local PostgreSQL volume:

```powershell
docker compose down -v
```

Only use `-v` when existing local data can be discarded.

## First-Time Usage

The application does not load sources from a configuration file. Create at least one source through the API.

### Add a Greenhouse source

```json
[
  {
    "companyName": "DoorDash",
    "careerUrl": "https://job-boards.greenhouse.io/doordashusa",
    "active": true,
    "provider": "GREENHOUSE"
  }
]
```

### Add a Lever source

```json
[
  {
    "companyName": "Grid",
    "careerUrl": "https://jobs.lever.co/Grid",
    "active": true,
    "provider": "LEVER"
  }
]
```

PowerShell example:

```powershell
curl -X POST "http://localhost:8080/api/sources" `
  -H "Content-Type: application/json" `
  -d "[{`"companyName`":`"Grid`",`"careerUrl`":`"https://jobs.lever.co/Grid`",`"active`":true,`"provider`":`"LEVER`"}]"
```

The provider value is required, case-sensitive, and must match a value supported by an implemented provider. Sources are deduplicated by exact `careerUrl`.

The scheduler can invoke its first run shortly after startup. After a run completes, Spring waits for the configured fixed delay before starting the next one. The default delay is 120,000 milliseconds, or two minutes.

To use a shorter delay during local testing:

Windows:

```powershell
.\gradlew.bat bootRun --args="--app.scheduler.ingestion-fixed-delay-ms=30000"
```

macOS/Linux:

```bash
./gradlew bootRun --args="--app.scheduler.ingestion-fixed-delay-ms=30000"
```

There is currently no manual ingestion endpoint.

## REST API

Base URL:

```text
http://localhost:8080
```

### Create job sources

```http
POST /api/sources
Content-Type: application/json
```

The endpoint accepts a JSON list. Each object contains:

| Field | Required | Description |
|---|---:|---|
| `companyName` | Yes | Display name stored with the source |
| `careerUrl` | Yes | Provider-hosted career-page URL |
| `active` | Yes | Whether the scheduler should process the source |
| `provider` | Yes | `GREENHOUSE` or `LEVER` |

Successful response:

```text
201 Created
```

### List job sources

```http
GET /api/sources
```

Example response:

```json
[
  {
    "companyName": "Grid",
    "careerUrl": "https://jobs.lever.co/Grid",
    "active": true,
    "provider": "LEVER"
  }
]
```

### List jobs

```http
GET /api/jobs?page=0&size=20
```

Pagination starts at page `0`. The defaults are `page=0` and `size=20`.

Example response:

```json
{
  "content": [
    {
      "id": 1,
      "title": "Software Engineer",
      "companyName": "Grid",
      "location": "Seattle, Washington",
      "department": "Engineering",
      "jobUrl": "https://jobs.lever.co/Grid/example-id"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

### Get job details

```http
GET /api/jobs/{id}
```

Example response:

```json
{
  "id": 1,
  "title": "Software Engineer",
  "companyName": "Grid",
  "location": "Seattle, Washington",
  "department": "Engineering",
  "jobUrl": "https://jobs.lever.co/Grid/example-id",
  "jobDescription": "Full job description text...",
  "discoveredAt": "2026-07-14T10:30:00"
}
```

A missing job ID returns `404 Not Found`.

### List ingestion runs

```http
GET /api/ingestion-runs
```

Example response:

```json
[
  {
    "id": 1,
    "startedAt": "2026-07-14T10:30:00",
    "completedAt": "2026-07-14T10:30:15",
    "status": "SUCCESS",
    "jobsFound": 25,
    "jobsInserted": 8,
    "jobsSkipped": 17,
    "errorMessage": null
  }
]
```

Possible statuses are `RUNNING`, `SUCCESS`, and `FAILED`.

### Get one ingestion run

```http
GET /api/ingestion-runs/{id}
```

The controller intends to return `404 Not Found` for a missing ID. The current service dereferences the missing entity before the controller receives it, so a nonexistent ID may currently return a server error instead.

## Scheduler Behavior

Scheduling is enabled with `@EnableScheduling`. `JobIngestionScheduler` uses a fixed delay configured by:

```properties
app.scheduler.ingestion-fixed-delay-ms=120000
```

For each run, the scheduler:

1. Creates an `ingestion_run` row with status `RUNNING`.
2. Loads active job sources.
3. Resolves each source through `JobBoardProviderFactory`.
4. Scrapes and parses provider-specific listing pages.
5. Filters jobs by software-related title and department keywords.
6. Skips jobs already stored for the same source and external ID.
7. Fetches descriptions for new, relevant jobs.
8. Saves new postings.
9. Marks the run `SUCCESS`, or `FAILED` when an exception escapes ingestion.

An `AtomicBoolean` guard prevents overlapping executions within one application instance.

## Filtering and Deduplication

`SoftwareJobFilter` checks the lowercase combination of a job's title and department for any of these terms:

```text
engineering
software
backend
frontend
full stack
fullstack
java
platform
infrastructure
distributed systems
api
microservices
cloud
```

Jobs are deduplicated by the database constraint:

```text
job_source_id + external_job_id
```

The same external ID can exist under different sources, while repeated runs for the same source do not insert it again.

## Database Tables

Hibernate maps three main tables.

### `job_sources`

- `id`
- `company_name`
- `career_url`
- `active_status`
- `provider`
- `created_at`
- `updated_at`

### `job_postings`

- `id`
- `external_job_id`
- `job_source_id`
- `title`
- `department`
- `location`
- `job_url`
- `job_description`
- `discovered_at`
- `created_at`
- `updated_at`

The table has a unique constraint on `job_source_id` and `external_job_id`.

### `ingestion_run`

- `id`
- `started_at`
- `completed_at`
- `status`
- `jobs_found`
- `jobs_inserted`
- `jobs_skipped`
- `error_message`

## Build and Test

Run the complete test suite.

Windows:

```powershell
.\gradlew.bat test
```

macOS/Linux:

```bash
./gradlew test
```

Run only the Lever tests:

Windows:

```powershell
.\gradlew.bat test --tests "*Lever*"
```

macOS/Linux:

```bash
./gradlew test --tests "*Lever*"
```

Build the executable JAR:

```powershell
.\gradlew.bat clean build
```

The output is written under `build/libs/`.

## Troubleshooting

### Database variables are missing

For local `bootRun`, ensure `.env` exists in the repository root and contains:

```properties
DATABASE_NAME=jobIngestion
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password
```

### The application cannot connect to PostgreSQL

Check that the database is running:

```powershell
docker compose ps
```

From the host machine, PostgreSQL is available at `localhost:5432`. From another Compose container, use `db:5432`.

### PostgreSQL rejects `LEVER` with `job_sources_provider_check`

A database created before Lever support may retain an older check constraint. `ddl-auto=update` may not update that existing constraint.

Preserve existing data by updating the local constraint:

```sql
ALTER TABLE job_sources
DROP CONSTRAINT job_sources_provider_check;

ALTER TABLE job_sources
ADD CONSTRAINT job_sources_provider_check
CHECK (provider IN ('GREENHOUSE', 'LEVER', 'WORKDAY'));
```

For disposable local data, recreate the volume instead:

```powershell
docker compose down -v
docker compose up -d
```

### No jobs appear in `/api/jobs`

Check the following:

1. `GET /api/sources` returns at least one source.
2. The source has `"active": true`.
3. The provider is `GREENHOUSE` or `LEVER` and matches the career URL.
4. An ingestion run has completed.
5. The postings match at least one software-filter keyword.
6. The postings were not already stored by an earlier run.
7. Application logs do not show connection or parsing failures.

Greenhouse listing pages must match the selectors used by `GreenhouseParser`. Lever listing pages must contain `div.posting` elements, while detail pages must contain `[data-qa=job-description]` for a nonempty description.

### pgAdmin cannot connect

From pgAdmin running in Compose, use:

```text
Host: db
Port: 5432
Database: jobIngestion
Username: postgres
Password: password
```

From a host-installed database client, use `localhost` as the host.

## Adding a Provider

1. Add the provider value to `JobBoardProviderType`.
2. Implement `JobBoardProvider` and annotate it with `@Component`.
3. Add provider-specific scraper and parser components.
4. Return listing results through the shared `ScrapedJob` record.
5. Implement `getProviderType()` with the matching enum value.
6. Add listing-parser, detail-parser, and provider tests.
7. Update the database constraint through a schema migration.

`JobBoardProviderFactory` automatically registers Spring-managed `JobBoardProvider` implementations. Do not add provider-specific conditions to `JobIngestionService`.

## Current Limitations

- Only Greenhouse and Lever are implemented.
- There is no manual ingestion endpoint.
- Source APIs currently support create and list operations only.
- A missing ingestion-run ID may return a server error instead of a clean `404`.
- Filtering is keyword-based and can produce false positives or false negatives.
- External HTML changes can break provider selectors.
- A provider or source failure can cause the overall scheduled run to be marked failed.
- Closed postings are not marked inactive or removed.
- Retries, rate limiting, and per-provider metrics are not implemented.
- The in-memory overlap guard protects only one application instance.

## Roadmap

- Add Ashby support
- Add Workday support
- Add a manual ingestion endpoint
- Add retries, rate limiting, and source-level failure isolation
- Add provider-level metrics and observability
- Detect and deactivate closed postings
- Introduce versioned database migrations
- Improve source validation and API error handling

## Contributing

Contributions, issue reports, and architectural feedback are welcome. When adding a provider, keep job-board-specific behavior inside its provider package and include representative parser tests.
