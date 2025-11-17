#!/usr/bin/env bash
# Daemon wrapper for auto_push.sh
# Behavior:
# - Wait 1 hour before the first run
# - Then run auto_push.sh
# - Sleep a random interval between 45 and 75 minutes, run again, repeat

set -euo pipefail

REPO_DIR="/Users/shah_makhdum/Desktop/Project/gpa_calculator"
SCRIPT="$REPO_DIR/scripts/auto_push.sh"
LOG="$REPO_DIR/auto_push_daemon.log"

exec >>"$LOG" 2>&1

echo "auto_push_daemon starting: $(date -u '+%Y-%m-%dT%H:%M:%SZ')"

if [ ! -x "$SCRIPT" ]; then
  echo "auto_push script not executable or not found: $SCRIPT"; exit 1
fi

# First run after 1 hour
FIRST_SLEEP_SECONDS=$((60*60))
echo "Sleeping first for $FIRST_SLEEP_SECONDS seconds (1 hour)"
sleep "$FIRST_SLEEP_SECONDS"

while true; do
  echo "Running auto_push at $(date -u '+%Y-%m-%dT%H:%M:%SZ')"
  "$SCRIPT" || echo "auto_push failed at $(date)"

  # random sleep between 45 and 75 minutes (2700 - 4500 seconds)
  MIN=2700
  MAX=4500
  RANGE=$((MAX-MIN+1))
  # Use bash random
  RAND=$((RANDOM % RANGE + MIN))
  echo "Sleeping for $RAND seconds (~$((RAND/60)) minutes)"
  sleep "$RAND"
done
