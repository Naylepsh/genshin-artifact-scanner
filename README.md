# Genshin Artifact Scanner

## Setup

- Download Tesseract: `scoop install tesseract`
- Fill in `.env` (look at `.env.example` for example)

## Scanning

- Open the artifact inventory (be sure to stay at the very top of the artifact list)
- Run the app: `sbt run`
- Wait till the console displays `Done` message
- Exported file should now be in the directory specified in `env` under the name `artifact-export`