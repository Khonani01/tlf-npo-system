# TLF Connect — PHP API Documentation

## Overview

This folder contains the PHP JSON API for the TLF Connect website and Android application.

The backend uses PHP, MySQL and PDO prepared statements. User passwords are stored using `password_hash()` and verified using `password_verify()`.

## Local API Base URL

`http://localhost/tlf-connect/api`

This address is for development with XAMPP. A deployed API URL must be provided to the integration team separately.

## Available API Endpoints

| Endpoint | Method | Purpose | Authentication |
|---|---|---|---|
| `health.php` | GET | Check whether the API is running | No |
| `register.php` | POST | Register a new user | No |
| `login.php` | POST | Authenticate a user | No |
| `news.php` | GET | Retrieve news and events | No |
| `volunteers.php` | POST | Submit a volunteer application | Yes |
| `donations.php` | GET | Retrieve the logged-in user's donation history | Yes |

## Registration

**Endpoint:** `POST /register.php`

**Content-Type:** `application/json`

Example request:

`{"full_name":"Test User","email":"test@example.com","password":"TestPass123!","phone_number":"0712345678"}`

The `phone_number` field is optional.

Successful registration returns HTTP 201. Duplicate email addresses return HTTP 409. Invalid registration details return HTTP 422.

## Login

**Endpoint:** `POST /login.php`

**Content-Type:** `application/json`

Example request:

`{"email":"test@example.com","password":"TestPass123!"}`

Successful login returns HTTP 200 and establishes a PHP session using the `TLFSESSID` cookie.

Incorrect credentials return HTTP 401.

## News and Events

**Endpoint:** `GET /news.php`

Returns up to 50 news and events records from the `NewsEvents` table in JSON format.

## Volunteer Applications

**Endpoint:** `POST /volunteers.php`

Requires an authenticated PHP session.

Example request:

`{"skills":"Community outreach","availability":"Weekends"}`

Successful submission returns HTTP 201 with a volunteer ID and Pending status.

## Donation History

**Endpoint:** `GET /donations.php`

Requires an authenticated PHP session.

Returns up to 50 donation records belonging to the logged-in user.

**Important:** This endpoint retrieves donation history only. It does not process payments or create new donations.

## Database Configuration

The API reads its database configuration from these environment variables:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASS`

Real database passwords must never be committed to GitHub.

## Authentication and Integration

Authentication uses PHP sessions rather than JWT tokens.

Clients must retain and send the session cookie after login when accessing protected endpoints.

The Android application will require a network-accessible API address rather than its own localhost address. Cross-origin website integration may require additional cookie and CORS configuration.

## Automated Testing

The GitHub Actions workflow performs PHP syntax checks, imports a temporary MySQL test database, runs 17 automated API tests, and builds the Android debug APK.

Test files are located in `api/tests/`.

The test folder must be excluded from public deployment.

## Project Repository

Repository: `Khonani01/tlf-npo-system`

Backend development branch: `feature/vukosi-api`

The integration team should coordinate deployment URLs, authentication requirements and API changes before connecting the website and Android application.
