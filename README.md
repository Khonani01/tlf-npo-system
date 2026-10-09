# TLF Connect

TLF Connect is the website and Android app for the Tshwane Leadership Foundation (TLF), a non-profit organisation in Pretoria. It was built by a five-person student team for the XISD6329 Work Integrated Learning module at Rosebank College.

## Purpose

TLF needs a simple way to share its news, events and reports, take volunteer registrations, receive messages and show how people can donate. TLF Connect gives the foundation one system for this, with a website for the office and an Android app for the community.

## Features

- Register and log in with email and password, or with Google
- News and Events
- Reports
- Contact form
- Volunteer registration
- Donate information
- Profile and Settings
- Admin tab on the website to view volunteer registrations

## How it is built

| Part | Technology |
|---|---|
| Android app | Kotlin, Jetpack Compose (package `org.tlf.connect`) |
| Sign-in | Firebase Authentication (Google sign-in) |
| API | PHP JSON API |
| Database | MySQL (`tlf_db`) on InfinityFree |
| Website | HTML, CSS, JavaScript, PHP |
| Source control | GitHub |
| Planning | Azure DevOps Boards |
| Build and test | GitHub Actions |

The app does not connect to MySQL directly. It sends requests to the PHP API, and the API reads and writes the database. Passwords are stored as hashes using `password_hash`.

## Design

[Add: short description of the screens and navigation, with 2 or 3 screenshots from `docs/screenshots`.]

## Project structure

```
tlf-npo-system/
  android-app/     Android Studio project (Kotlin, Compose)
  ui-ux/           Wireframes, web designs, site map and user flow diagrams
  docs/            Screenshots, test plan, user guide
  [website and API folders to be added by the website and API owners]
```

## Getting started

### Website
1. Copy the project files to your web host (InfinityFree) or a local PHP server.
2. Import the database and set the connection details in `db_connect.php`.

### Android app
1. Clone the repository and switch to the app branch: `git checkout feature/android-app`
2. Open the `android-app` folder in Android Studio.
3. Ask the team lead for `google-services.json` and place it in `android-app/app/`. This file is private and is not stored in the repository.
4. Let Gradle sync, then run the app on an emulator or a phone.

## Branches

- `main`: final, working version
- `dev`: shared development branch
- `feature/*`: one branch per piece of work, merged into `dev` through a pull request

## GitHub Actions

[Add after Vukosi finishes the workflow: workflow file name, what triggers it (push and pull request), and what it does (build the app and run the tests).]

The workflow needs the `google-services.json` file, which is stored as a GitHub repository secret and written to the project during the build.

## Testing

The manual test plan is in `docs/TLF_Connect_Manual_Test_Plan.xlsx`. Bugs are tracked on the Azure DevOps board.

## Team

| Name | Student number | Main area |
|---|---|---|
| Khonani Mutobvu (team leader) | ST10439622 | Project setup, DevOps, testing, documentation |
| Khumbelo | [add] | App screens, database |
| Charity | [add] | App navigation, login, API integration |
| Vukosi | [add] | API, GitHub Actions |
| Ofentse | [add] | Website, demo video, presentation |

## Release notes

See `RELEASE_NOTES.md`.
