# Subtask modeling: self-referential Todo, capped at one level

We modeled subtasks as `Todo` itself, with a nullable, self-referential `parent` field, rather than introducing a separate `Subtask` entity. A top-level todo is simply a `Todo` with `parent == null`. Nesting is capped at one level in the service layer (a `Todo` with a non-null `parent` cannot itself become a parent) rather than in the schema, so the constraint can be relaxed later without a migration if deeper nesting is ever wanted. Deleting a parent cascade-deletes its subtasks, since a subtask has no independent meaning once the todo it decomposes is gone.

## Considered Options

- **Separate `Subtask` entity** — rejected. Would need its own repository, DTOs, and validation rules duplicating most of `Todo`'s, for a concept (a todo with a parent) that isn't actually lesser in this domain — subtasks have their own due dates and completion state just like any todo.
- **Unlimited nesting** — rejected for now. No requirement calls for a true tree; capping at one level keeps completion semantics and the UI simple. The self-referential model doesn't foreclose this — only the service-layer check would need to change.
- **Orphan subtasks on parent delete** — rejected. Leaves meaningless todos with a dangling parent reference and no clear reason to exist independently.
