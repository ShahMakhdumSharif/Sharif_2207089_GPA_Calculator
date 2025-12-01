# Sharif_2207089_GPA_Calculator

This JavaFX GPA calculator now includes:

- SQLite database persistence (courses saved to ~/.gpa_calculator.db)
- Observable Course model (JavaFX properties)
- Background concurrency (DB operations run off the UI thread)
- JSON export (writes ~/.gpa_courses.json)

How to run (Maven, on macOS):

```bash
mvn javafx:run
```

Notes for lab:
- The SQLite DB file is created at: ~/.gpa_calculator.db
- Exported JSON is written to: ~/.gpa_courses.json
- Commit history should include the changes to add DB and concurrency code.

Make a ZIP of the project root for the lab submission. Ensure you commit frequently while working.
