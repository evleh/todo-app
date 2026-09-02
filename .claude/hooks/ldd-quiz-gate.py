#!/usr/bin/env python3
"""PreToolUse gate for `git commit`: blocks non-trivial commits until Claude
has quizzed the user on the staged diff. See docs/agents/learning-driven-development.md.
"""
import hashlib
import json
import os
import re
import subprocess
import sys

LOGIC_EXTENSIONS = {".java", ".ts", ".vue", ".js"}
TRIVIAL_EXTENSIONS = {".md", ".json", ".yml", ".yaml", ".xml", ".gitignore", ".gitattributes"}
MIN_CHANGED_LINES = 10


def run(cmd, cwd):
    return subprocess.run(cmd, capture_output=True, text=True, cwd=cwd).stdout


def allow():
    sys.exit(0)


def main():
    data = json.load(sys.stdin)
    command = data.get("tool_input", {}).get("command", "")

    if not re.search(r"\bgit\s+commit\b", command):
        allow()

    cwd = data.get("cwd", os.getcwd())

    staged = run(["git", "diff", "--cached"], cwd)
    if not staged.strip():
        allow()

    numstat = run(["git", "diff", "--cached", "--numstat"], cwd)
    total_changed = 0
    has_logic_file = False
    for line in numstat.splitlines():
        parts = line.split("\t")
        if len(parts) != 3:
            continue
        added, removed, path = parts
        added = 0 if added == "-" else int(added)
        removed = 0 if removed == "-" else int(removed)
        ext = os.path.splitext(path)[1]
        if ext in LOGIC_EXTENSIONS:
            has_logic_file = True
        if ext not in TRIVIAL_EXTENSIONS:
            total_changed += added + removed

    if not has_logic_file or total_changed <= MIN_CHANGED_LINES:
        allow()

    diff_hash = hashlib.sha256(staged.encode()).hexdigest()
    marker_path = os.path.join(cwd, ".claude", "ldd-quiz-passed")

    if os.path.exists(marker_path):
        with open(marker_path) as f:
            if f.read().strip() == diff_hash:
                allow()

    reason = (
        "LDD gate: this commit touches non-trivial logic and hasn't been quizzed yet.\n"
        "Before retrying `git commit`, ask the user 2-3 questions about the staged "
        "changes: a couple of quick-recall questions plus at least one \"explain this "
        "in your own words\" question, covering both what changed and the relevant "
        "programming principle(s) behind it. Grade their answers yourself.\n"
        "Only once they demonstrably understand the change, clear the gate by running "
        "exactly this, then retry the commit:\n"
        f"  mkdir -p .claude && echo '{diff_hash}' > .claude/ldd-quiz-passed\n"
        "If they can't explain it, don't clear the gate — help them understand the "
        "code first, then quiz again."
    )

    print(json.dumps({
        "hookSpecificOutput": {
            "hookEventName": "PreToolUse",
            "permissionDecision": "deny",
            "permissionDecisionReason": reason,
        }
    }))
    sys.exit(0)


if __name__ == "__main__":
    main()
