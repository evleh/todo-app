# CI Pipeline

## Problem Statement

`todo-app` has no CI. There is no `.github/` directory, so backend and frontend changes only get validated when someone remembers to run `./mvnw test` and `npm run build` locally before pushing. Nothing stops a broken build or a failing test from landing on `master`.

## Solution

A GitHub Actions workflow that builds and tests both the backend and frontend on every pull request into `master` and every push to `master`, with both jobs required to pass before a PR can merge.

Deployment is explicitly out of scope: no hosting target exists yet for this app, so CD is a separate decision for later, once somewhere to deploy to has been chosen.

## Decisions

- **Platform**: GitHub Actions — the repo already lives on GitHub.
- **Triggers**: `pull_request` targeting `master`, and `push` to `master`.
- **Backend job**: uses a GitHub Actions `services:` Postgres container configured to match the existing hardcoded test config in `backend/src/test/resources/application.properties` (`todoapp_test` / `eww` / `eww`, port `5432`), rather than migrating tests to Testcontainers first. JDK 21 (matches `backend/pom.xml`). Runs `./mvnw test` with working directory `backend/`, since the Maven wrapper only exists there — the root `pom.xml` is just a parent aggregator (`<module>backend</module>`) with no wrapper of its own. No CI secrets are needed: the JWT secret used in tests is already hardcoded in the same properties file.
- **Frontend job**: `npm install` then `npm run build` in `frontend/` (this already runs `vue-tsc -b && vite build`, so typecheck and build are one step). No ESLint config or test framework exists yet, so linting and frontend tests are intentionally left out of this pipeline — a separate decision if/when they're introduced.
- **Branch protection**: both jobs are set as required status checks on `master`, configured in GitHub repo settings (not expressible in the workflow YAML itself) once the workflow has run at least once.

## Deferred Work

Backend tests hit a real Postgres via a hardcoded connection string rather than Testcontainers. CI works around this by matching the service container to that hardcoded config instead of fixing the underlying test setup. A future session should consider migrating to Testcontainers so both CI and local dev get an ephemeral, self-configuring database. This is a deliberate deferral, not an oversight — not recorded as an ADR since the decision is cheap to reverse later (touches only CI config + test properties, no architectural lock-in).

## Verification

1. Push to a feature branch, open a PR against `master` — both jobs should appear as checks and go green.
2. Break something deliberately (e.g. a failing assertion) on that branch, push, confirm the relevant check goes red.
3. Push directly to `master` (or merge the PR) — confirm the `push` trigger also fires.
4. Only after a successful run: configure branch protection and confirm a new PR can't merge while a check is red.
