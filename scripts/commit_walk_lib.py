"""Walk master/main history: working tree shows one commit at a time (git diff HEAD)."""

from __future__ import annotations

import json
import shutil
import subprocess
import sys
from pathlib import Path

MOVE_FIRST = "first"
MOVE_LAST = "last"
MOVE_NEXT = "next"
MOVE_PREVIOUS = "previous"

DEMO_START_SUBJECT = "Каркас бота с long polling"

WALK_SCRIPT_PATHS = (
    "scripts/commit_walk_lib.py",
    "scripts/first",
    "scripts/last",
    "scripts/next",
    "scripts/previous",
)

_REPO_ROOT: Path | None = None


def _repo_root() -> Path:
    global _REPO_ROOT
    if _REPO_ROOT is not None:
        return _REPO_ROOT
    result = subprocess.run(
        ["git", "rev-parse", "--show-toplevel"],
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise SystemExit(result.stderr.strip() or "Not inside a git repository.")
    _REPO_ROOT = Path(result.stdout.strip())
    return _REPO_ROOT


def _git(*args: str, check: bool = True) -> str:
    result = subprocess.run(
        ["git", "-C", str(_repo_root()), *args],
        capture_output=True,
        text=True,
        check=False,
    )
    if check and result.returncode != 0:
        stderr = result.stderr.strip() or result.stdout.strip()
        raise SystemExit(stderr)
    return result.stdout


def _git_dir() -> Path:
    git_dir = Path(_git("rev-parse", "--git-dir").strip())
    if not git_dir.is_absolute():
        git_dir = _repo_root() / git_dir
    return git_dir


def _state_path() -> Path:
    return _git_dir() / "commit-walk-state.json"


def _walk_scripts_snapshot_dir() -> Path:
    return _git_dir() / "commit-walk-scripts"


def _empty_tree() -> str:
    return _git("hash-object", "-t", "tree", "/dev/null").strip()


def _empty_parent_commit() -> str:
    marker = _git_dir() / "commit-walk-empty-commit"
    if marker.is_file():
        cached = marker.read_text(encoding="utf-8").strip()
        if subprocess.run(
            ["git", "-C", str(_repo_root()), "cat-file", "-e", cached],
            capture_output=True,
        ).returncode == 0:
            return cached
    sha = _git("commit-tree", _empty_tree(), "-m", "commit-walk-empty").strip()
    marker.write_text(sha, encoding="utf-8")
    return sha


def _sync_walk_scripts_snapshot(ref: str) -> None:
    root = _repo_root()
    snap = _walk_scripts_snapshot_dir()
    snap.mkdir(exist_ok=True)
    allowed = {Path(rel).name for rel in WALK_SCRIPT_PATHS}
    for rel in WALK_SCRIPT_PATHS:
        src = root / rel
        dest = snap / Path(rel).name
        if src.is_file():
            shutil.copy2(src, dest)
        elif not dest.is_file():
            dest.write_text(_git("show", f"{ref}:{rel}"), encoding="utf-8")
    for cached in snap.iterdir():
        if cached.is_file() and cached.name not in allowed:
            cached.unlink()


def _clean_build_artifacts() -> None:
    root = _repo_root()
    for name in ("target", "out", "build"):
        path = root / name
        if path.is_dir():
            shutil.rmtree(path, ignore_errors=True)


def _restore_walk_scripts() -> None:
    root = _repo_root()
    snap = _walk_scripts_snapshot_dir()
    scripts_dir = root / "scripts"
    if scripts_dir.is_dir():
        for entry in scripts_dir.iterdir():
            if entry.name == "__pycache__":
                continue
            if entry.is_file():
                entry.unlink()
    for rel in WALK_SCRIPT_PATHS:
        name = Path(rel).name
        if not (snap / name).is_file():
            continue
        dest = root / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(snap / name, dest)
        subprocess.run(
            ["git", "-C", str(root), "reset", "-q", "HEAD", "--", rel],
            check=False,
        )


def _apply_commit_view(sha: str, parent: str | None) -> None:
    _git("reset", "--hard", "-q", "HEAD")
    _git("clean", "-fd", "-q")
    parent_ref = parent if parent is not None else _empty_parent_commit()
    _git("checkout", "--detach", "-q", parent_ref)
    _git("reset", "--hard", "-q", parent_ref)
    _git("clean", "-fd", "-q")
    _git("read-tree", "--reset", "-u", sha)
    _restore_walk_scripts()
    for rel in WALK_SCRIPT_PATHS:
        subprocess.run(
            ["git", "-C", str(_repo_root()), "reset", "-q", "HEAD", "--", rel],
            check=False,
        )
    _clean_build_artifacts()


def _master_ref() -> str:
    for ref in ("master", "main"):
        if subprocess.run(
            ["git", "-C", str(_repo_root()), "rev-parse", "--verify", ref],
            capture_output=True,
        ).returncode == 0:
            return ref
    raise SystemExit("Neither master nor main branch exists.")


def _master_tip(ref: str) -> str:
    return _git("rev-parse", ref).strip()


def _ancestor_of(ancestor: str, desc: str) -> bool:
    return (
        subprocess.run(
            ["git", "-C", str(_repo_root()), "merge-base", "--is-ancestor", ancestor, desc],
            capture_output=True,
        ).returncode
        == 0
    )


def _demo_start_index(commits: list[str]) -> int:
    for index, sha in enumerate(commits):
        subject = _git("log", "-1", "--format=%s", sha).strip()
        if subject == DEMO_START_SUBJECT:
            return index
    raise SystemExit(f'Commit not found on master: "{DEMO_START_SUBJECT}"')


def _load_commits(ref: str, tip: str, cached: list[str] | None) -> list[str]:
    if cached and _ancestor_of(cached[0], tip) and _ancestor_of(cached[-1], tip):
        if _git("rev-parse", cached[-1]).strip() == tip:
            return cached
    lines = _git("rev-list", "--reverse", ref).splitlines()
    commits = [line.strip() for line in lines if line.strip()]
    if not commits:
        raise SystemExit(f"No commits on {ref}.")
    return commits


def _load_state() -> dict | None:
    path = _state_path()
    if not path.is_file():
        return None
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (json.JSONDecodeError, OSError):
        return None


def _save_state(ref: str, tip: str, commits: list[str], index: int) -> None:
    payload = {
        "ref": ref,
        "master_tip": tip,
        "commits": commits,
        "index": index,
        "sha": commits[index],
    }
    _state_path().write_text(json.dumps(payload, separators=(",", ":")), encoding="utf-8")


def _resolve_index(
    state: dict | None, commits: list[str], move: str, demo_start: int
) -> tuple[int, bool]:
    at_edge = False
    last_index = len(commits) - 1

    if state is None or "index" not in state:
        if move == MOVE_LAST:
            index = last_index
        else:
            index = demo_start
    else:
        index = int(state["index"])
        if "sha" in state:
            try:
                index = commits.index(state["sha"])
            except ValueError:
                index = min(max(index, demo_start), last_index)

    if move == MOVE_FIRST:
        index = demo_start
    elif move == MOVE_LAST:
        index = last_index
    elif move == MOVE_NEXT:
        if index >= last_index:
            at_edge = True
            index = last_index
        else:
            index += 1
    elif move == MOVE_PREVIOUS:
        if index <= demo_start:
            at_edge = True
            index = demo_start
        else:
            index -= 1
    else:
        raise ValueError(move)

    return index, at_edge


def _print_commit(commits: list[str], index: int, demo_start: int) -> None:
    sha = commits[index]
    subject = _git("log", "-1", "--format=%s", sha).strip()
    body = _git("log", "-1", "--format=%B", sha).rstrip()
    short = _git("rev-parse", "--short", sha).strip()
    demo_total = len(commits) - demo_start
    demo_pos = index - demo_start + 1
    print(f"[{demo_pos}/{demo_total}] {short} {subject}")
    if body and body != subject:
        print(body)
    print("---")


def navigate(move: str) -> None:
    ref = _master_ref()
    tip = _master_tip(ref)
    _sync_walk_scripts_snapshot(ref)
    state = _load_state()
    cached = state.get("commits") if state and state.get("ref") == ref and state.get("master_tip") == tip else None
    if cached is None and state and state.get("commits") and state.get("ref") == ref:
        cached = state["commits"]
    commits = _load_commits(ref, tip, cached)
    demo_start = _demo_start_index(commits)

    index, at_edge = _resolve_index(
        state if state and state.get("ref") == ref else None,
        commits,
        move,
        demo_start,
    )
    if at_edge and move in (MOVE_NEXT, MOVE_PREVIOUS):
        if move == MOVE_NEXT:
            print(f"Уже последний коммит на {ref}.", file=sys.stderr)
        else:
            print(f"Уже первый коммит демо ({DEMO_START_SUBJECT}).", file=sys.stderr)

    sha = commits[index]
    parent = commits[index - 1] if index > 0 else None
    _apply_commit_view(sha, parent)
    _save_state(ref, tip, commits, index)
    _print_commit(commits, index, demo_start)

    if at_edge and move in (MOVE_NEXT, MOVE_PREVIOUS):
        raise SystemExit(1)
