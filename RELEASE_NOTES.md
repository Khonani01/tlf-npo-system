# Release notes

## Version 1.0 (version code 1), 16 November 2026

These notes list what changed since the prototype, which was the set of wireframes drawn in Task 1.

### New in this version
- Working Android app in Kotlin and Jetpack Compose (`org.tlf.connect`)
- Register and login with email and password through Firebase Authentication
- Google sign-in
- Home screen with a time-based greeting and the user's name
- Donate screen with preset and custom amounts, once-off or monthly, saved to Firestore
- Volunteer form with skill areas, a Select All option and six programmes, saved to Firestore
- Profile screen with edit name, change photo, privacy and security advice, language and log out
- Signed release build, version 1.0
- Code comments on all screens

### Innovative features added in this version
- Google single sign-in
- English and isiZulu language option, including the tab labels
- Dark mode
- A 10 second connection timeout on the forms so the app does not freeze without internet
- Profile photo chosen with the system photo picker, so the app only sees the photo that was selected

### Behind the app
- PHP JSON API with register, login, news, volunteers and donations endpoints, and automated tests (not connected to the app)
- GitHub Actions workflow for PHP checks and an Android debug build
- Six-table database design with 10 demonstration rows per table
- Website on InfinityFree with Google sign-in and an admin dashboard

### Not in this version
- News and Events, Reports and Contact screens in the app
- Payment processing for donations
- Connection of the app to the PHP API
