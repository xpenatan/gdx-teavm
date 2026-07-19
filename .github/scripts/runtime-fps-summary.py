#!/usr/bin/env python3

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path


PLATFORMS = ("linux", "macos", "windows", "android", "ios")
EXAMPLES = ("basic", "freetype", "controllers")
LOG_SUFFIXES = {".log", ".txt"}
FPS_PATTERN = re.compile(r"FPSLogger[^\r\n]*?\bfps\s*:\s*(\d+(?:\.\d+)?)", re.IGNORECASE)


def extract_fps(text: str) -> list[float]:
    return [float(match.group(1)) for match in FPS_PATTERN.finditer(text)]


def collect_fps(proof_dir: Path) -> list[float]:
    values: list[float] = []
    if not proof_dir.is_dir():
        return values

    fps_log = proof_dir / "fps.log"
    if fps_log.is_file():
        values = extract_fps(fps_log.read_text(encoding="utf-8", errors="replace"))
        if values:
            return values

    for log_file in sorted(proof_dir.rglob("*")):
        if not log_file.is_file() or log_file.suffix.lower() not in LOG_SUFFIXES:
            continue
        if log_file == fps_log:
            continue
        values.extend(extract_fps(log_file.read_text(encoding="utf-8", errors="replace")))
    return values


def format_fps(value: float) -> str:
    return f"{value:.1f}"


def build_summary(root: Path) -> tuple[str, list[str]]:
    lines = [
        "# Runtime example FPS summary",
        "",
        "Collected from libGDX `FPSLogger` output during the GitHub Actions runtime proofs.",
        "",
        "| Platform | Example | Samples | Average FPS | Min FPS | Max FPS | Latest FPS |",
        "| --- | --- | ---: | ---: | ---: | ---: | ---: |",
    ]
    missing: list[str] = []

    for platform in PLATFORMS:
        for example in EXAMPLES:
            values = collect_fps(root / platform / example)
            if not values:
                missing.append(f"{platform}/{example}")
                lines.append(f"| {platform} | {example} | 0 | — | — | — | — |")
                continue

            average = sum(values) / len(values)
            lines.append(
                f"| {platform} | {example} | {len(values)} | {format_fps(average)} | "
                f"{format_fps(min(values))} | {format_fps(max(values))} | {format_fps(values[-1])} |"
            )

    if missing:
        lines.extend(
            [
                "",
                "## Missing samples",
                "",
                *[f"- `{proof}`" for proof in missing],
            ]
        )

    return "\n".join(lines) + "\n", missing


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("root", type=Path, help="runtime proof directory")
    parser.add_argument(
        "--require-complete",
        action="store_true",
        help="fail when any platform/example proof has no FPS samples",
    )
    args = parser.parse_args()

    root = args.root
    summary, missing = build_summary(root)
    output = root / "fps-summary.md"
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(summary, encoding="utf-8")

    annotation = "error" if args.require_complete else "warning"
    for proof in missing:
        print(f"::{annotation} title=Missing FPS samples::{proof} did not contain FPSLogger output")
    print(f"Wrote {output} with {len(PLATFORMS) * len(EXAMPLES) - len(missing)} FPS result(s).")
    return 1 if args.require_complete and missing else 0


if __name__ == "__main__":
    raise SystemExit(main())
