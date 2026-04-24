# Quiz Leaderboard System
### SRM Internship Assignment | Bajaj Finserv Health | Java Qualifier

This project is complete and ready for review. It polls the quiz API 10 times, removes duplicate events, calculates the final leaderboard, and submits it exactly once.

## What the interviewer sees first

![Successful run output](Output/Output.png)

The screenshot shows the full verified run, including polling, deduplication, aggregation, submission, and final build success.

## One-line summary

The app is a Spring Boot command-line program that solves the leaderboard problem end to end without requiring a web server.

## What is implemented

The current implementation covers the full assignment flow:

1. Poll the validator 10 times with a 5-second delay between calls.
2. Deduplicate events using `roundId + participant`.
3. Sum scores per participant and sort descending.
4. Submit the leaderboard once and log the response.

## Architecture

```text
QuizLeaderboardApplication
  |
  v
QuizOrchestrator
  |
  +--> QuizPollerService
  |       GET /quiz/messages?regNo=X&poll=0..9
  |
  +--> EventDeduplicator
  |       Remove duplicate roundId + participant pairs
  |
  +--> ScoreAggregator
  |       Build sorted leaderboard
  |
  +--> SubmitService
    POST /quiz/submit exactly once
```

## Project layout

```text
quiz-leaderboard/
├── pom.xml
├── mvnw / mvnw.cmd
├── Output/Output.png
├── README.md
└── src/main
    ├── java/com/quiz
    │   ├── QuizLeaderboardApplication.java
    │   ├── config/AppConfig.java
    │   ├── model
    │   └── service
    └── resources/application.properties
```

## How to verify without running anything

If the interviewer only wants proof, the screenshot in `Output/Output.png` is the main evidence. It shows:

- all 10 polls completed
- raw events collected
- duplicates removed
- final leaderboard produced
- leaderboard submitted
- build success at the end

## How to run locally

### Prerequisites

- Java 17 or later
- Windows, macOS, or Linux
- Internet access for the quiz API

Maven is already included through the wrapper, so no separate Maven installation is required.

### 1. Open the project folder

```powershell
cd C:\Users\gamer\Downloads\quiz-leaderboard\quiz-leaderboard
```

### 2. Set your registration number

The app reads the registration number from the environment variable `QUIZ_REG_NO`.

```powershell
set QUIZ_REG_NO=YOUR_REG_NO
```

### 3. Run the application

```powershell
cmd /c "set QUIZ_REG_NO=YOUR_REG_NO && mvnw.cmd spring-boot:run"
```

### 4. Recommended run for a stable demo

If the validator is slow or returns a temporary `503`, use a higher retry count:

```powershell
cmd /c "set QUIZ_REG_NO=YOUR_REG_NO && mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--quiz.max-retries-per-poll=15"
```

## How to verify the output

During a successful run, the interviewer should look for these lines:

- `Quiz pipeline started`
- `Polling started: regNo=...`
- `Polling complete: rawEvents=...`
- `Deduplication complete: raw=..., unique=...`
- `Aggregation complete: participants=..., totalScore=...`
- `Submission response summary: ...`
- `Final leaderboard:`
- `Result: SUBMITTED (validator summary mode)`
- `Quiz pipeline finished`

If these appear in order, the pipeline is working correctly.

## Expected behavior

- The app should finish in about 50 seconds because it waits 5 seconds between 10 polls.
- The final leaderboard is sorted by total score in descending order.
- The submit endpoint is called only once.
- If the validator returns summary metadata instead of correctness fields, the app still treats the run as successful and logs the response clearly.

## Build and test

```powershell
cmd /c mvnw.cmd test
```

To create a jar:

```powershell
cmd /c mvnw.cmd clean package -DskipTests
```

## Push to GitHub

After updating the README and screenshot, push the repository with:

```powershell
git status
git add README.md Output/Output.png src/main/java src/main/resources pom.xml mvnw mvnw.cmd .mvn
git commit -m "Improve README and add verified output"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

If the remote already exists, skip the `git remote add origin` line.

## Notes for the interviewer

This codebase is intentionally structured so the main behavior is easy to inspect quickly:

- `QuizPollerService` handles the API polling.
- `EventDeduplicator` removes repeated events.
- `ScoreAggregator` produces the final sorted leaderboard.
- `SubmitService` sends the payload once and logs the validator response.

The run in `Output/Output.png` is the proof of execution, so the project can be understood at a glance even before running it again.

## Author

SRM Institute of Science and Technology

Registration No: RA2311004010136
