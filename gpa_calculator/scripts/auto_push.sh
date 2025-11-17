#!/usr/bin/env bash
# Auto git add/commit/push script
#  - safe: only commits when there are changes
#  - does: git add -A, commit with timestamp, pull --rebase, push
#  - logs to AUTO_PUSH_LOG if set or default file in repo

set -euo pipefail

# CONFIG
REPO_DIR="/Users/shah_makhdum/Desktop/Project/gpa_calculator"
LOG_FILE="${AUTO_PUSH_LOG:-$REPO_DIR/auto_push.log}"
GIT_AUTHOR_NAME="Auto Backup"
GIT_AUTHOR_EMAIL="noreply@localhost"
# Remote repo to push to. Set to your repo (SSH form recommended).
# Change this if you prefer HTTPS. Provided repo:
# https://github.com/ShahMakhdumSharif/Sharif_2207089_GPA_Calculator
REPO_URL="git@github.com:ShahMakhdumSharif/Sharif_2207089_GPA_Calculator.git"
# Remote branch to push backups into (default: auto-backup)
AUTO_PUSH_BRANCH="${AUTO_PUSH_BRANCH:-auto-backup}"

exec >>"$LOG_FILE" 2>&1

printf "\n=== auto_push run: %s ===\n" "$(date -u '+%Y-%m-%dT%H:%M:%SZ')"

cd "$REPO_DIR" || { echo "Cannot cd to $REPO_DIR"; exit 1; }

# Ensure we're in a git repo
if [ ! -d .git ]; then
  echo "Not a git repository: $REPO_DIR"; exit 1
fi

# Ensure origin remote exists; if missing, add it using REPO_URL
if ! git remote get-url origin >/dev/null 2>&1; then
  echo "No origin remote configured. Adding origin -> $REPO_URL"
  if ! git remote add origin "$REPO_URL"; then
    echo "Failed to add origin remote. Please configure manually."; exit 4
  fi
fi

# Determine branch
BRANCH=$(git rev-parse --abbrev-ref HEAD || echo "main")

# Check for changes
if [ -z "$(git status --porcelain)" ]; then
  echo "No changes to commit."; exit 0
fi

# Configure author (optional, useful when machine user not set)
git config user.name "$GIT_AUTHOR_NAME" || true
git config user.email "$GIT_AUTHOR_EMAIL" || true

# Add and commit
git add -A
TIMESTAMP=$(date -u '+%Y-%m-%dT%H:%M:%SZ')
COMMIT_MSG="Auto backup: $TIMESTAMP"
if git commit -m "$COMMIT_MSG"; then
  echo "Committed: $COMMIT_MSG"
else
  echo "Nothing to commit after add (concurrent change?)."; exit 0
fi

# Push current HEAD to the dedicated backup branch on the remote. This avoids
# modifying the user's normal remote branches; the backup branch will be
# created if it does not exist.
echo "Pushing current HEAD to origin/$AUTO_PUSH_BRANCH..."
if git push origin "HEAD:refs/heads/$AUTO_PUSH_BRANCH"; then
  echo "Push to $AUTO_PUSH_BRANCH successful."; exit 0
else
  echo "Push failed. Manual intervention required."; exit 3
fi
