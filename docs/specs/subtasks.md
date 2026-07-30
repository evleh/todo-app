# Subtasks

## Problem Statement

Users often have todos that aren't really a single action — they're really a small checklist of steps (e.g. "Plan birthday party" involves booking a venue, sending invites, and ordering a cake). Right now the app only supports flat todos, so users either cram all the steps into one todo's text, or create several unrelated top-level todos that lose their connection to the thing they're actually part of. There's no way to break a todo down into smaller pieces while keeping them grouped under the task they belong to.

## Solution

A `Todo` can have **subtasks** — smaller todos that belong to it and are shown alongside it. A subtask is completed, due-dated, and deleted independently of its parent, but is always owned by the same user, always deleted along with its parent, and can only ever be one level deep (a subtask cannot itself have subtasks). The top-level todo list shows only top-level todos; subtasks appear embedded when you view their parent.

This follows the domain model already recorded in [`CONTEXT.md`](../../CONTEXT.md) and [ADR-0002](../adr/0002-subtask-as-self-referential-todo.md).

## User Stories

1. As a todo app user, I want to add subtasks to a todo, so that I can break a larger task down into smaller, trackable steps.
2. As a todo app user, I want to see a todo's subtasks embedded when I view it, so that I can see the full breakdown of work in one place without a second request.
3. As a todo app user, I want to mark a subtask done independently of its parent, so that I can track partial progress without forcing a particular meaning onto the parent's own completion state.
4. As a todo app user, I want to mark a parent todo done independently of its subtasks, so that I retain full control over what "done" means for the parent.
5. As a todo app user, I want to give a subtask its own due date, so that I can schedule individual steps separately from the todo they belong to.
6. As a todo app user, I want a subtask's due date to have no forced relationship to its parent's due date, so that reasonable real-world cases (e.g. a parent with no due date at all) aren't blocked by rigid validation.
7. As a todo app user, I want subtasks to always belong to the same owner as their parent, so that ownership stays simple and consistent with how the rest of the app already works.
8. As a todo app user, I want deleting a parent todo to delete all its subtasks, so that I don't end up with orphaned, meaningless subtasks cluttering my data.
9. As a todo app user, I want to be prevented from adding a subtask to another subtask, so that the hierarchy stays simple, predictable, and never more than one level deep.
10. As a todo app user, I want my top-level todo list to show only top-level todos, so that subtasks don't clutter the main view.
11. As a todo app user, I want subtasks displayed in the order they were created, so that I'm not forced to manage manual ordering for what is effectively a simple checklist.
12. As a todo app user, I want a subtask's parent to be fixed once it's created, so the hierarchy I've built doesn't shift unexpectedly underneath me.
13. As a todo app user, I want to be blocked from creating or viewing subtasks under a todo I don't own, so that my data stays private from other users, consistent with how top-level todos already work.
14. As an admin, I want to access any user's todos and their subtasks, so that I can provide support or oversight consistent with existing admin behavior on top-level todos.
15. As a todo app user, I want to delete an individual subtask without affecting its parent or sibling subtasks, so that I can remove a step that's no longer needed.
16. As a todo app user, I want to update a subtask's task text, due date, and done flag like any other todo, so that I can correct or refine it after creation.
17. As a todo app user, I want an attempt to create a subtask under a nonexistent parent to fail with a clear error, so I get useful feedback instead of a confusing failure.
18. As a todo app user, I want an attempt to create a subtask under a todo that is itself already a subtask to fail with a clear error, so the one-level rule is enforced rather than silently ignored.
19. As a todo app developer relying on the API, I want a dedicated endpoint for creating subtasks, so that the one-level nesting rule is structurally obvious in the API shape rather than something every client has to remember to enforce itself.

## Implementation Decisions

- **Entity**: `Todo` gains a nullable, self-referential `@ManyToOne parent` field (per ADR-0002), plus a `@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)` `subtasks` collection — the same cascade/orphan-removal pattern already used for `MyUser.todos`, so deleting a parent cascades to its subtasks via JPA rather than manual service code.
- **One-level cap**: enforced in `TodoService`, not the schema. Creating a subtask under a `Todo` whose own `parent != null` is rejected. This is a business-rule check, not a database constraint, so it can be relaxed later without a migration.
- **Ownership**: a subtask's owner is always set to its parent's owner at creation time; there is no independent owner/assignment field for subtasks. The existing `assertOwnerOrAdmin` check in `TodoService` is reused unmodified — since a subtask's owner is always its parent's owner, checking ownership on the parent is sufficient.
- **New service method**: a `createSubtask(parentId, TodoCreateRequest, UserPrincipal)`-shaped method that: loads the parent by id (404 via the existing `TodoIdNotFoundException` if missing), enforces ownership on the parent via `assertOwnerOrAdmin`, rejects if the parent already has a non-null `parent` (one-level violation — new exception type, mapped to `400 Bad Request` since it's a structurally invalid request rather than a resource conflict), then creates and saves the subtask with the parent's owner.
- **New endpoint**: `POST /todos/{parentId}/subtasks`, reusing the existing `TodoCreateRequest` DTO unchanged (no `parentId` field in the body — the path variable supplies it), returning `201 Created` with a `TodoResponse`.
- **Existing endpoints reused unmodified** for everything else: `PUT /todos/{id}` and `DELETE /todos/{id}` work on subtasks exactly as they do on top-level todos, since a subtask is just a `Todo` row.
- **`TodoResponse`** gains a `subtasks: List<TodoResponse>` field, populated for top-level todos and always empty for a subtask's own response (a subtask can't have subtasks). No separate computed progress fields (e.g. counts) are added — the client derives anything it needs from the embedded list.
- **`GET /todos` (readAll)** is scoped to top-level todos only (`parent == null`) — requires a new repository query (e.g. `findByOwnerAndParentIsNull` / an admin equivalent without the owner filter).
- **`GET /todos/{id}`** returns the embedded `subtasks` list when the id belongs to a top-level todo.
- **Repository**: add a method to fetch a `Todo`'s children (e.g. `findByParent(Todo parent)`), used when building the embedded `subtasks` list in `toResponse`.
- **Ownership enforcement must be explicit in the service layer**, not via `@PostAuthorize`/`@PreAuthorize` on the controller. There is no `@EnableMethodSecurity` anywhere in the app, so those annotations are currently inert (see Further Notes) — the new subtask endpoints must follow the `update`/`deleteByID` pattern of an explicit `assertOwnerOrAdmin` call inside the service, not the `read` pattern of an annotation that never actually runs.

## Testing Decisions

- **Single seam: HTTP/controller-level integration tests.** Following the existing precedent set by `AuthControllerTest` (`@SpringBootTest` + `@AutoConfigureMockMvc`, real Postgres via `docker compose up`, data seeded directly through repositories in `@BeforeEach`, assertions on concrete HTTP status codes and response bodies through the real security filter chain) rather than adding a second, lower-altitude seam of mocked `TodoService` unit tests. This is the highest available seam and keeps subtask behavior verified as an outside observer would see it — including things a mocked-repository unit test can't verify, like the real JPA cascade-delete behavior.
- **New test class** covering, at minimum:
  - Creating a subtask under an owned parent → `201`, correct fields, subsequently appears in the parent's embedded `subtasks` list.
  - Creating a subtask under another user's parent → `403`.
  - Creating a subtask under a nonexistent parent id → `404`.
  - Creating a subtask under an existing subtask (nesting violation) → `400`.
  - `GET /todos` returns only top-level todos.
  - `GET /todos/{parentId}` embeds its subtasks.
  - Completing a subtask does not change the parent's `done` flag, and vice versa.
  - Deleting a parent cascades — its subtasks are gone afterward.
  - Updating a subtask via the existing `PUT /todos/{id}` works exactly as it does for top-level todos.
  - Deleting an individual subtask leaves its parent and any sibling subtasks untouched.
- **What makes a good test here**: assert only externally observable behavior — HTTP status codes and response bodies (including follow-up `GET`s to confirm state) — never internal method calls or repository interactions, since this is a controller-level seam.
- Note: `TodoServiceTests` (Mockito-based unit tests) and a dedicated `TodoControllerTest` don't currently exist for most `Todo` operations — this spec deliberately doesn't add unit-level coverage as a second seam. If a future contributor wants defense-in-depth unit coverage of the one-level-nesting rule specifically (a pure business-rule check with no I/O), that can be added later without contradicting this decision, but it isn't required here.

## Out of Scope

- Reparenting or moving a subtask to a different parent, or promoting it back to top-level.
- Manual ordering/reordering of subtasks — display order is creation order only.
- Nesting deeper than one level.
- Assigning a subtask to a different owner than its parent (no delegation/collaboration feature).
- Server-computed progress fields (e.g. subtask counts) on `TodoResponse`.
- Cascading completion in either direction between parent and subtask.
- Any constraint between a subtask's due date and its parent's due date.
- Fixing the `@EnableMethodSecurity`/`@PostAuthorize` no-op issue on `TodoController.read` (see Further Notes) — pre-existing, unrelated to this feature.
- Frontend implementation — the todo management frontend doesn't exist yet.

## Further Notes

- **Pre-existing authorization gap, not introduced by this feature**: `TodoController.read` (`GET /todos/{id}`) is annotated `@PostAuthorize("hasRole('ADMIN') || returnObject.ownerId().equals(authentication.principal.userId)")`, but there is no `@EnableMethodSecurity` anywhere in the application. Without it, Spring never wraps the controller in the AOP proxy that evaluates `@PostAuthorize`/`@PreAuthorize`, so the annotation never runs — it's inert. `TodoService.read` has no ownership check of its own either, unlike `update`/`deleteByID`, which correctly enforce ownership via an explicit `assertOwnerOrAdmin` call inside the service. Net effect: any authenticated user can currently read any other user's todo by id via `GET /todos/{id}`. This is out of scope to fix here, but it's the reason this spec insists on explicit service-layer ownership checks for the new subtask endpoints rather than reusing the `read` method's (non-functional) annotation-based pattern.
- **Global task-text uniqueness**: `TodoService.create` rejects a new todo if `todoRepository.findByTask(...)` already finds a match — this check is global across all users and todos, not scoped per-owner. Since subtasks are just `Todo` rows, this same check will apply to them, meaning two different users' subtasks (or a subtask and an unrelated top-level todo) can't share exact task text (e.g. two different todos both trying to have a subtask literally named "Step 1" would conflict). Not changed by this spec, but worth being aware of as a likely source of confusing `409` responses once subtasks are in regular use.
- This spec was produced via a `/grilling` session (see conversation history) that resolved the domain design questions now recorded in `CONTEXT.md` and ADR-0002; this document should be read alongside both.
- **Publishing status**: `gh` CLI isn't installed in this environment, so this spec has **not** been published to GitHub Issues or labeled `ready-for-agent` as the standard process calls for. Once `gh` is available and authenticated, publish this file's contents as an issue and apply the `ready-for-agent` label per `docs/agents/issue-tracker.md`.
