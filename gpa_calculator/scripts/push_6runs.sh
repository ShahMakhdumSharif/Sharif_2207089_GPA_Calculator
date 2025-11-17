#!/usr/bin/env bash
# Run auto_push.sh 6 times: first immediately, then 5 more at randomized ~hourly intervals
set -euo pipefail

REPO_DIR="/Users/shah_makhdum/Desktop/Project/gpa_calculator"
SCRIPT="$REPO_DIR/scripts/auto_push.sh"
LOG="$REPO_DIR/auto_push_6runs.log"

exec >>"$LOG" 2>&1

echo "push_6runs starting at $(date -u '+%Y-%m-%dT%H:%M:%SZ')"

if [ ! -x "$SCRIPT" ]; then
  echo "auto_push script not executable or not found: $SCRIPT"; exit 1
fi

for i in $(seq 1 6); do
  echo "Run #$i at $(date -u '+%Y-%m-%dT%H:%M:%SZ')"
  if "$SCRIPT"; then
    echo "auto_push succeeded for run #$i"
  else
    echo "auto_push failed for run #$i"
  fi

  if [ $i -lt 6 ]; then
    # Sleep random between 45 and 75 minutes
    MIN=2700
    MAX=4500
    RANGE=$((MAX-MIN+1))
    SLEEP_SEC=$((RANDOM % RANGE + MIN))
    echo "Sleeping $SLEEP_SEC seconds (~$((SLEEP_SEC/60)) minutes) before next run"
    sleep "$SLEEP_SEC"
  fi
done

echo "push_6runs completed at $(date -u '+%Y-%m-%dT%H:%M:%SZ')"
