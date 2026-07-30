# Todo App

Tracks a user's todos, each of which may be broken down into subtasks.

## Language

**Todo**:
A task owned by exactly one user, with an optional due date and a done flag. May be a top-level todo (no parent) or a subtask (has a parent).

**Subtask**:
A `Todo` whose `parent` points to another `Todo`. Nesting is capped at one level — a subtask cannot itself have subtasks. A subtask always has the same owner as its parent, is completed independently of its parent, and has no constraint on its own due date relative to the parent's. Deleting the parent deletes its subtasks.
_Avoid_: Child todo, checklist item
