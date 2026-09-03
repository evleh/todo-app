# Learning-Driven Development (LDD)

This repo is a testbed for learning how to best use Claude Code as a development tool ([README](../../README.md)). LDD is the practice of actively preventing **ignorance debt**: code that exists in the repo which the human maintainer could not explain or rebuild without the agent.

## Ignorance debt

Incurred whenever Claude writes code the user hasn't actively engaged with — read, questioned, or partly written themselves — regardless of whether the code is correct. Passively reading an insight or explanation does not clear it; asking a question, filling in a `TODO(human)` section, or requesting a change does.

This is additive to the Learning output style (insights, `[JUNIOR]/[MID]/[SENIOR]` tags, `TODO(human)` requests) already active for this user across all projects — LDD is a further, enforced layer on top of it, not a replacement.

## The commit-time gate

`.claude/hooks/ldd-quiz-gate.py`, wired up as a `PreToolUse` hook on `Bash(git commit *)` in `.claude/settings.json`.

**Scope**: fires only when Claude runs `git commit` inside a session. A manual commit from the terminal or an IDE bypasses it — this is a deliberate trade-off (session-context quizzing over blanket coverage), not an oversight.

**Trivial-change filter**: the hook inspects the staged diff (`git diff --cached --numstat`). It only engages if at least one `.java`, `.ts`, `.vue`, or `.js` file changed and the total changed lines (excluding `.md`/`.json`/`.yml`/`.yaml`/`.xml`/gitignore-type files) exceeds 10. Docs, config, and small diffs pass through silently. `git commit --amend` is treated like any other commit — it re-triggers if the amended diff is itself non-trivial.

**Gate mechanics**: for a non-trivial, not-yet-cleared diff, the hook denies the tool call and hands Claude a `permissionDecisionReason` instructing it to:

1. Ask the user 2-3 questions about the staged changes — a mix of quick-recall and at least one "explain this in your own words" question, covering both what changed and the relevant programming principle(s) behind it.
2. Grade the answers itself, in the conversation — there's no separate grading call, Claude is already the one with full session context.
3. If the user demonstrably understands the change, clear the gate by writing the diff's hash to a marker file (exact command is in the hook's denial reason) and retry the commit.
4. If they can't explain it, don't clear the gate — help them understand the code first, then quiz again.

The marker lives at `.claude/ldd-quiz-passed` (gitignored, so still outside version control) and is keyed to a hash of the exact staged diff, so any further staged change invalidates it and re-triggers the quiz.

**Known limitation**: the marker-file loop-prevention pattern is not part of Claude Code's documented hook API — it's a practical workaround. If commits start looping or the gate never clears, that mechanism is the first thing to check.

## When a skill says "this repo practices LDD"

Before committing non-trivial changes, expect (and cooperate with) the quiz gate above rather than trying to route around it.
