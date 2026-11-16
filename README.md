# TLF Connect

TLF Connect is an Android app and a website for the Tshwane Leadership Foundation (TLF), a non-profit organisation in Pretoria. It was built by a five-person student team for the XISD6329 Work Integrated Learning module at Rosebank College.

Demo video: https://youtu.be/MaEVtpNKNh8

Live website: https://tlfconnect.infy.click

## Purpose

TLF needs a simple way to reach its community, take volunteer applications and receive donation pledges. The Android app lets people create an account or sign in with Google, donate, apply to volunteer and manage their profile. The website adds news, events, reports and a contact form, and gives staff an admin dashboard.

## Features

Android app
- Register and log in with email and password, or with Google
- Home screen with a time-based greeting
- Donate: preset and custom amounts, once-off or monthly (records a pledge, no payment is processed)
- Volunteer application with skill areas and programme
- Profile: edit name, change photo, privacy and security advice, English or isiZulu, dark mode, log out

Website
- Home, About, News and Events, Reports, Get Involved, Donate
- Sign in with email or Google
- Admin dashboard to manage news, events, volunteers, reports and messages

## How it is built

| Part | Technology | Data |
|---|---|---|
| Android app | Kotlin, Jetpack Compose (package `org.tlf.connect`), Firebase Authentication | Firebase Cloud Firestore (users, volunteers, donations) |
| Website | HTML, CSS, JavaScript, PHP on InfinityFree, Firebase Authentication for Google sign-in | MySQL |
| API | PHP JSON API with PDO prepared statements, `password_hash`, PHP sessions | MySQL (`tlf_db`, six tables) |
| Source control | GitHub | |
| Build and test | GitHub Actions | |

The app stores its data in Firebase and does not call the PHP API. The API was tested locally and in GitHub Actions and is not deployed to the live website.

## Design

The design uses one task per screen, large touch targets, high contrast orange and black colours and a fixed bottom navigation bar with four tabs: Home, Donate, Volunteer and Profile. The Task 1 wireframes are in `ui-ux`. The final app uses orange and black instead of the navy blue of the wireframes. News and Events, Reports and Contact are on the website and not in the app.

## Project structure

```
tlf-npo-system/
  android-app/     Android Studio project (Kotlin, Compose)
  api/             PHP JSON API and tests
  ui-ux/           Wireframes, web designs, site map and user flow diagrams
  .github/         GitHub Actions workflow
```

## Getting started

### Android app
1. Clone the repository and open the `android-app` folder in Android Studio.
2. Ask the team leader for `google-services.json` and place it in `android-app/app/`. This file is private and is not stored in the repository.
3. Let Gradle sync, then run the app on an emulator or a phone.

### Website
Upload the website files to a PHP host and set the database connection details in `db_connect.php`.

## Branches

- `main`: final, working version
- `dev`: shared development branch
- `feature/*`: one branch per piece of work, merged through a pull request

## GitHub Actions

The workflow `.github/workflows/tlf-ci.yml` runs on pushes to `dev` and `feature/**` and on pull requests to `dev`. The PHP job checks syntax, creates a temporary MySQL database and runs smoke and functional tests. The Android job builds a debug APK. Run #20 passed both jobs, with 10 of 10 smoke tests and 7 of 7 functional tests passing.

## Team

| Name | Student number | Main area |
|---|---|---|
| Khonani Mutobvu | ST10439622 | Team leader, README, release notes |
| Khumbelo Tshikororo | ST10441369 | UI/UX design, database, release signing |
| Ofentse Mashigoane | ST10456959 | Website, deployment, demo video |
| Vukosi Sono | ST10453078 | API, GitHub Actions |
| Charity Taulene | ST10438951 | Android app |

## Release notes

See `RELEASE_NOTES.md`.
