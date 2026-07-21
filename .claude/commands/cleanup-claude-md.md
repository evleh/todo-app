---
description: Audit and slim down a CLAUDE.md for context efficiency. Proposes a plan before writing anything.
argument-hint: "[path-to-claude-md]"
allowed-tools: Read, Glob, Grep, Edit, Write
disable-model-invocation: true
---

Audit a CLAUDE.md for context efficiency. It loads in full on every session
start, so every line costs context budget permanently. Make it lean and
high-signal without losing anything useful.

Target file: $ARGUMENTS (if empty, use ./CLAUDE.md)

## Current target file
@CLAUDE.md

## Other memory files to cross-check
!`ls -1 ~/.claude/CLAUDE.md ./CLAUDE.local.md .claude/rules/ 2>/dev/null`

STEP 1 — Inventory (do not edit yet)
- Read the target file plus any nested CLAUDE.md, ~/.claude/CLAUDE.md, and
  .claude/rules/ shown above.
- List every distinct entry/instruction in the target file as a numbered list.

STEP 2 — Classify each entry into exactly one bucket:
KEEP      — needed in EVERY session AND project-specific: build/test/lint
commands, project layout, terminology, "always do X" rules that
materially change your decisions and can't be inferred from code.
GLOBAL    — personal/cross-project preference. Belongs in ~/.claude/CLAUDE.md.
RULE      — deep, path-specific, or only-sometimes-relevant. Belongs in a
path-scoped file under .claude/rules/.
REFERENCE — duplicates README/architecture/docs. Replace with an @path ref.
CUT       — generic filler ("write clean code"), stale plans, checklists,
one-off notes, or anything auto memory would capture itself.

STEP 3 — Propose the plan
For each entry: [current text] → [bucket] → [destination] → [1-line reason].
Flag contradictions and duplicates. Then show the rewritten CLAUDE.md in full.

CONSTRAINTS
- Target ~20–80 lines; hard ceiling ~150. Overflow → push more into .claude/rules/.
- Move, don't delete: GLOBAL/RULE/REFERENCE content gets relocated, not lost.
- Keep instructions (rules) and reference (project facts) in separate sections.
- Preserve command strings and terminology verbatim — don't paraphrase those.
- Do not invent new conventions; only reorganize what exists.

STOP after STEP 3 and wait for my explicit approval before writing any file.