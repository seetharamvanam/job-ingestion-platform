# Job Ingestion Platform

Job Ingestion Platform is a Spring Boot service that collects software-related job postings from configured job boards, stores them in PostgreSQL, and exposes REST APIs for reading job sources, ingested jobs, and ingestion run history.

The current implementation is focused on Greenhouse job boards. It scrapes active job sources on a fixed schedule, filters jobs by software/engineering keywords, stores new postings only once per source and external job id, and saves job descriptions from each job detail page when available.

## What The Application Does

The application has four main responsibilities:

1. Manage job sources, such as a company name and its Greenhouse careers URL.
2. Periodically scrape active sources for job postings.
3. Store relevant software-related jobs in PostgreSQL.
4. Provide APIs to view job postings and ingestion run results.

At a high level, the flow is:

```text
Job source API
  -> PostgreSQL job_sources table
  -> scheduled ingestion
  -> Greenhouse scraper
  -> Greenhouse parser
  -> software job filter
  -> job_postings table
  -> jobs API
```

## Tech Stack

- Java 21
- Spring Boot 3.5.0
- Gradle 9.5.1 wrapper
- Spring Web
- Spring Data JPA
- PostgreSQL
- Jsoup for HTML scraping/parsing
- Lombok
- Docker Compose for local PostgreSQL and pgAdmin

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
│   ├── detail/          # Job detail page parsing
│   ├── filter/          # Software-job filtering rules
│   ├── ingestion/       # Main ingestion orchestration
│   ├── ingestionrun/    # Ingestion run status/history APIs
│   ├── job/             # Job entities, repositories, services, APIs
│   ├── parser/          # Job board list-page parsing
│   ├── scheduler/       # Fixed-delay ingestion scheduler
│   ├── scraper/         # HTTP scraping using Jsoup
│   └── source/          # Job source entities, services, APIs
├── src/main/resources/
│   ├── application.properties
│   ├── banner.txt
│   └── job-sources.json
└── src/test/java/
```

## Prerequisites

Install these before running the application:

- JDK 21
- Docker Desktop, if you want to use the provided PostgreSQL setup
- A terminal from the repository root
- An API client such as curl, Postman, Insomnia, or a browser for GET endpoints

You do not need to install Gradle manually. Use the included wrapper:

- Windows: `.\gradlew.bat`
- macOS/Linux: `./gradlew`

## Environment Variables

The app imports an optional `.env` file from the repository root:

```properties
DATABASE_NAME=jobIngestion
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password
```

These values match the provided `docker-compose.yaml`.

Create `D:\job-ingestion-platform\.env` with those values for local development. The `.env` file is ignored by git, so do not commit it.

The database URL is built from `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/${DATABASE_NAME}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
app.scheduler.ingestion-fixed-delay-ms=120000
```

`spring.jpa.hibernate.ddl-auto=update` means Hibernate creates or updates tables automatically when the app starts. This is convenient for local development, but be careful with it in production.

## Start The Local Database

From the repository root:

```powershell
docker compose up -d db pgadmin
```

This starts:

- PostgreSQL on `localhost:5432`
- pgAdmin on `http://localhost:8181`

Default PostgreSQL credentials:

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

To stop the containers:

```powershell
docker compose down
```

To stop containers and remove the database volume, which deletes local data:

```powershell
docker compose down -v
```

Only use `-v` when you intentionally want a clean database.

## Run The Application Locally

1. Start PostgreSQL:

```powershell
docker compose up -d db
```

2. Create the `.env` file shown above.

3. Start the Spring Boot application:

```powershell
.\gradlew.bat bootRun
```

On macOS/Linux:

```bash
./gradlew bootRun
```

The application listens on:

```text
http://localhost:8080
```

## Build And Test

Run tests:

```powershell
.\gradlew.bat test
```

Build the application jar:

```powershell
.\gradlew.bat clean build
```

After a successful build, the jar is created under:

```text
build/libs/
```

## Docker Image

The `Dockerfile` runs a previously built Spring Boot jar:

```powershell
.\gradlew.bat clean build
docker build -t job-ingestion-platform .
```

The provided Docker Compose file currently starts PostgreSQL and pgAdmin only. It does not define an application service. For the simplest local development workflow, run the app with `bootRun` and keep PostgreSQL in Docker.

## First-Time Usage

The application needs at least one active job source before ingestion can collect jobs.

Important: `src/main/resources/job-sources.json` contains a DoorDash Greenhouse source, but the startup loader is currently disabled because `StartupRunner` is commented out. That file is not automatically imported at application startup in the current code.

Create a source through the API after the app starts:

```powershell
curl -X POST "http://localhost:8080/api/sources" `
  -H "Content-Type: application/json" `
  -d "[{`"companyName`":`"DoorDash`",`"careerUrl`":`"https://job-boards.greenhouse.io/doordashusa`",`"active`":true}]"
```

Equivalent JSON body:

```json
[
  {
    "companyName": "DoorDash",
    "careerUrl": "https://job-boards.greenhouse.io/doordashusa",
    "active": true
  }
]
```

Then wait for the scheduler to run. The default delay is 120,000 ms, or 2 minutes. There is currently no manual ingestion API endpoint.

To make ingestion run more frequently during local testing, override the scheduler delay:

```powershell
.\gradlew.bat bootRun --args="--app.scheduler.ingestion-fixed-delay-ms=30000"
```

That runs the scheduler every 30 seconds after each completed run.

## REST API

Base URL:

```text
http://localhost:8080
```

### Create Job Sources

```http
POST /api/sources
Content-Type: application/json
```

Request body:

```json
[
  {
    "companyName": "DoorDash",
    "careerUrl": "https://job-boards.greenhouse.io/doordashusa",
    "active": true
  }
]
```

Response:

```text
201 Created
```

Notes:

- The endpoint accepts a list of sources.
- Sources are deduplicated by `careerUrl`.
- `active` should be a JSON boolean, not a quoted string.
- Only active sources are scraped by the scheduler.

### List Job Sources

```http
GET /api/sources
```

Example:

```powershell
curl "http://localhost:8080/api/sources"
```

Response shape:

```json
[
  {
    "companyName": "DoorDash",
    "careerUrl": "https://job-boards.greenhouse.io/doordashusa",
    "active": true
  }
]
```

### List Jobs

```http
GET /api/jobs?page=0&size=20
```

Example:

```powershell
curl "http://localhost:8080/api/jobs?page=0&size=20"
```

Response shape:

```json
{
  "content": [
    {
      "id": 1,
      "title": "Software Engineer",
      "companyName": "DoorDash",
      "location": "New York, NY",
      "department": "Engineering",
      "jobUrl": "https://..."
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

Pagination starts at page `0`.

### Get Job Details

```http
GET /api/jobs/{id}
```

Example:

```powershell
curl "http://localhost:8080/api/jobs/1"
```

Response shape:

```json
{
  "id": 1,
  "title": "Software Engineer",
  "companyName": "DoorDash",
  "location": "New York, NY",
  "department": "Engineering",
  "jobUrl": "https://...",
  "jobDescription": "Full job description text...",
  "discoveredAt": "2026-06-24T02:30:00"
}
```

If a job id is not found, this endpoint returns `404 Not Found`.

### List Ingestion Runs

```http
GET /api/ingestion-runs
```

Example:

```powershell
curl "http://localhost:8080/api/ingestion-runs"
```

Response shape:

```json
[
  {
    "id": 1,
    "startedAt": "2026-06-24T02:30:00",
    "completedAt": "2026-06-24T02:30:15",
    "status": "SUCCESS",
    "jobsFound": 100,
    "jobsInserted": 25,
    "jobsSkipped": 75,
    "errorMessage": null
  }
]
```

Possible statuses:

- `RUNNING`
- `SUCCESS`
- `FAILED`

### Get One Ingestion Run

```http
GET /api/ingestion-runs/{id}
```

Example:

```powershell
curl "http://localhost:8080/api/ingestion-runs/1"
```

Current caveat: the controller is written as if it can return `404 Not Found`, but the service dereferences the entity before checking for null. A missing ingestion run id may currently produce a server error instead of a clean 404.

## Scheduler Behavior

Scheduling is enabled in `JobIngestionPlatformApplication` with `@EnableScheduling`.

The scheduler is implemented in `JobIngestionScheduler` and runs with:

```properties
app.scheduler.ingestion-fixed-delay-ms=120000
```

This means Spring waits for an ingestion run to complete, then waits the configured delay before starting the next run.

The scheduler uses an `AtomicBoolean` guard so a new ingestion run is skipped if a previous run is still active.

Each scheduler run:

1. Creates an `ingestion_run` row with status `RUNNING`.
2. Loads all active job sources from the database.
3. Scrapes and parses jobs from each source.
4. Saves new relevant jobs.
5. Marks the run `SUCCESS` with counts, or `FAILED` with an error message.

## Ingestion Rules

Only active job sources are scraped.

The current scraper/parser is Greenhouse-specific:

- `GreenhouseScraper` fetches HTML using Jsoup.
- `GreenhouseParser` parses department sections and job rows.
- `GreenhouseJobDetailParser` extracts text from `div.job__description.body`.

The software job filter keeps jobs whose title or department contains one of these keywords:

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

Jobs are deduplicated by:

```text
job_source_id + external_job_id
```

If a matching job already exists, it is skipped.

## Database Tables

Hibernate creates these main tables from JPA entities:

### job_sources

Stores configured career pages.

Key fields:

- `id`
- `company_name`
- `career_url`
- `active_status`
- `created_at`
- `updated_at`

### job_postings

Stores ingested jobs.

Key fields:

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

Unique constraint:

```text
job_source_id + external_job_id
```

### ingestion_run

Stores scheduler run history.

Key fields:

- `id`
- `started_at`
- `completed_at`
- `status`
- `jobs_found`
- `jobs_inserted`
- `jobs_skipped`
- `error_message`

## Common Local Workflow

1. Start PostgreSQL:

```powershell
docker compose up -d db
```

2. Create `.env`:

```properties
DATABASE_NAME=jobIngestion
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password
```

3. Start the app:

```powershell
.\gradlew.bat bootRun
```

4. Create at least one source:

```powershell
curl -X POST "http://localhost:8080/api/sources" `
  -H "Content-Type: application/json" `
  -d "[{`"companyName`":`"DoorDash`",`"careerUrl`":`"https://job-boards.greenhouse.io/doordashusa`",`"active`":true}]"
```

5. Wait for the scheduler to run.

6. Check ingestion history:

```powershell
curl "http://localhost:8080/api/ingestion-runs"
```

7. Check jobs:

```powershell
curl "http://localhost:8080/api/jobs?page=0&size=20"
```

## Troubleshooting

### The app fails to start because database variables are missing

Make sure `.env` exists in the repository root and contains:

```properties
DATABASE_NAME=jobIngestion
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password
```

### The app cannot connect to PostgreSQL

Check that Docker is running and the database container is up:

```powershell
docker compose ps
```

If needed, restart the database:

```powershell
docker compose down
docker compose up -d db
```

### No jobs appear in `/api/jobs`

Check these in order:

1. Confirm at least one source exists:

```powershell
curl "http://localhost:8080/api/sources"
```

2. Confirm the source has `"active": true`.
3. Wait for the scheduler interval to pass.
4. Check ingestion runs:

```powershell
curl "http://localhost:8080/api/ingestion-runs"
```

5. Check application logs for scrape or parsing failures.

### The source in `job-sources.json` did not load

That is expected in the current code. `StartupRunner` is commented out, so `JobSourceLoader` is not called during startup. Use `POST /api/sources` to create sources.

### Ingestion runs, but inserts zero jobs

Possible reasons:

- The source has no jobs matching the software keyword filter.
- The jobs were already inserted during a previous run.
- The Greenhouse page markup changed and the parser selectors no longer match.
- The external job board was temporarily unreachable.

### pgAdmin cannot connect to the database

When connecting from pgAdmin running in Docker, use the Docker Compose service name as the host:

```text
Host: db
Port: 5432
Database: jobIngestion
Username: postgres
Password: password
```

When connecting from your host machine, use:

```text
Host: localhost
Port: 5432
```

## Important Current Limitations

- Only Greenhouse-style job boards are supported by the current parser and scraper implementation.
- There is no manual ingestion trigger API.
- `job-sources.json` is present but not automatically loaded because `StartupRunner` is commented out.
- The Docker Compose file does not run the Spring Boot application container.
- Missing ingestion run ids may not return a clean `404` due to current service behavior.
- Filtering is keyword-based and may skip relevant jobs or include irrelevant ones.
- The application depends on external job board HTML structure, so scraper/parser behavior can break when those pages change.

## Development Notes

To add support for a new job board type:

1. Create a new `JobBoardScraper` implementation if fetching needs different behavior.
2. Create a new `JobBoardParser` implementation for the board's list page markup.
3. Create a new `JobDetailParser` implementation if detail pages use different markup.
4. Update ingestion orchestration to choose the correct parser/scraper per source type.
5. Add parser tests with representative HTML samples.

To change which jobs are considered relevant, edit the keyword list in `SoftwareJobFilter`.

To change the scheduler frequency, update:

```properties
app.scheduler.ingestion-fixed-delay-ms=120000
```

or override it at runtime:

```powershell
.\gradlew.bat bootRun --args="--app.scheduler.ingestion-fixed-delay-ms=30000"
```
