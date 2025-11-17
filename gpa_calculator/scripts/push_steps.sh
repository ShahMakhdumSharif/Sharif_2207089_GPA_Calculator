#!/usr/bin/env bash
# push_steps.sh
# Perform 6 staged pushes: each step adds only a selected group of files,
# commits, and pushes to origin:auto-backup. First run happens immediately.
# Subsequent runs sleep a randomized ~hour interval (45-75 minutes).

set -euo pipefail

REPO_DIR="/Users/shah_makhdum/Desktop/Project/gpa_calculator"
LOG="$REPO_DIR/push_steps.log"
REPO_URL="git@github.com:ShahMakhdumSharif/Sharif_2207089_GPA_Calculator.git"
AUTO_PUSH_BRANCH="auto-backup"
GIT_AUTHOR_NAME="Auto Backup"
GIT_AUTHOR_EMAIL="noreply@localhost"

exec >>"$LOG" 2>&1

echo "push_steps started at $(date -u '+%Y-%m-%dT%H:%M:%SZ')"

cd "$REPO_DIR" || { echo "Cannot cd to $REPO_DIR"; exit 1; }

# Ensure origin exists
if ! git remote get-url origin >/dev/null 2>&1; then
  echo "Adding origin $REPO_URL"
  git remote add origin "$REPO_URL" || { echo "Failed to add origin"; exit 1; }
fi

# Ensure git author
git config user.name "$GIT_AUTHOR_NAME" || true
git config user.email "$GIT_AUTHOR_EMAIL" || true

# Define step groups (tweak as needed)
step1=("pom.xml" "module-info.java" "src/main/java/com/example/App.java" "src/main/java/com/example/Course.java" "src/main/java/com/example/PrimaryController.java" "src/main/resources/com/example/primary.fxml")
step2=("src/main/java/com/example/SecondaryController.java")
step3=("src/main/resources/com/example/secondary.fxml")
step4=("src/main/java/com/example/ResultController.java" "src/main/resources/com/example/result.fxml")
step5=("scripts/auto_push.sh" "scripts/auto_push_daemon.sh" "scripts/push_6runs.sh")
step6=("README.md")

steps=(step1 step2 step3 step4 step5 step6)

echo "Will run ${#steps[@]} steps. Starting now. Logs: $LOG"

for i in $(seq 1 ${#steps[@]}); do
  name=${steps[$((i-1))]}
  echo "\n=== Step #$i -> $name at $(date -u '+%Y-%m-%dT%H:%M:%SZ') ==="
  files=()
  # The above is tricky; instead, handle per-name
  if [ "$name" = "step1" ]; then
    files=("${step1[@]}")
  elif [ "$name" = "step2" ]; then
    files=("${step2[@]}")
  elif [ "$name" = "step3" ]; then
    files=("${step3[@]}")
  elif [ "$name" = "step4" ]; then
    files=("${step4[@]}")
  elif [ "$name" = "step5" ]; then
    files=("${step5[@]}")
  elif [ "$name" = "step6" ]; then
    files=("${step6[@]}")
  else
    echo "Unknown step name: $name"; exit 1
  fi

  # Stage existing files only
  staged=()
  for f in "${files[@]}"; do
    if [ -e "$f" ]; then
      git add "$f" && staged+=("$f")
    else
      echo "Skipping missing: $f"
    fi
  done

  if [ ${#staged[@]} -eq 0 ]; then
    echo "No files staged for step $i (nothing to commit)."
  else
    COMMIT_MSG="Step $i push: ${staged[*]}"
    if git commit -m "$COMMIT_MSG"; then
      echo "Committed: $COMMIT_MSG"
      # push to backup branch
      if git push origin "HEAD:refs/heads/$AUTO_PUSH_BRANCH"; then
        echo "Pushed step $i to $AUTO_PUSH_BRANCH"
        echo "Commit: $(git rev-parse --short HEAD)"
      else
        echo "Push failed for step $i"
      fi
    else
      echo "Nothing to commit after staging for step $i"
    fi
  fi

  # Sleep randomized interval between steps except after last
  if [ $i -lt ${#steps[@]} ]; then
    MIN=2700
    MAX=4500
    SLEEP_SEC=$((RANDOM % (MAX-MIN+1) + MIN))
    echo "Sleeping $SLEEP_SEC seconds (~$((SLEEP_SEC/60)) minutes) before next step"
    sleep "$SLEEP_SEC"
  fi

done

echo "push_steps finished at $(date -u '+%Y-%m-%dT%H:%M:%SZ')"
