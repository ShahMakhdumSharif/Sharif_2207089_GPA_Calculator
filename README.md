# Sharif_2207089_GPA_Calculator
- `src/main/java/com/example/App.java`
	- Purpose: JavaFX entry point and simple scene manager.
	- Key methods: `start(Stage)` launches the primary scene using `primary.fxml`; `setRoot(String)` and `setRootParent(Parent)` change views.
- `src/main/java/com/example/Course.java`
	- Purpose: Model representing a course. Uses JavaFX properties (`StringProperty`, `DoubleProperty`).
	- Key fields: `title`, `code`, `teacher1`, `teacher2`, `credit`, `grade` and corresponding property accessors and getters/setters.
- `src/main/java/com/example/DatabaseHelper.java`
	- Purpose: Lightweight singleton wrapper around SQLite (JDBC) providing DB init and CRUD for `Course` objects.
	- Key methods: `initDB()`, `fetchAllCourses()`, `insertCourse(Course)`, `updateCourse(Course)`, `deleteCourseByCode(String)`.
	- Storage: `gpa_calculator.db` in the project working directory by default.
- `src/main/java/com/example/PrimaryController.java`
	- Purpose: Controller for the `primary.fxml` view (home/start screen).
	- Key actions: usually navigates to `secondary` view (data entry).

- `src/main/java/com/example/SecondaryController.java`
	- Purpose: Main data-entry controller where the user adds courses and triggers GPA calculation.
	- Key behavior: collects course fields from the form, stores courses in an `ObservableList`, inserts/updates via `DatabaseHelper` using a background `ExecutorService`, enables/disables the Calculate button based on entered credits, and exposes a JSON export action.
- `src/main/java/com/example/ResultController.java`
	- Purpose: Shows the GPA report and the list of entered courses. Provides Edit/Delete actions per course.
	- Key behavior: displays course summary, calls controllers to edit (re-open entry form populated for editing) and deletes from DB using a background thread.

- `src/main/resources/com/example/secondary.fxml` and `result.fxml`
	- Purpose: FXML layouts for the entry and result screens. Wire controls (`fx:id`) to controller fields and `onAction` handlers.