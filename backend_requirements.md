# Volunteer Registration & Management — Backend Requirements

*Organised by DevOps lifecycle stage: Build → Test → Deploy → Monitor*

## Build

- New `volunteers` table:
  - `volunteerID` (PK, auto-increment)
  - `fullName`
  - `email`
  - `phone`
  - `skill`
  - `eventChoice`
  - `message` (nullable)
  - `status` (enum: pending / approved / declined, default pending)
  - `submittedAt` (timestamp)
  - `reviewedBy` (FK → `users.userID`, nullable)
- `process_volunteer.php`: handles the `volunteer.html` form POST. Validates required fields server-side (name, email, phone, skill, event, terms), inserts into `volunteers`, redirects back with a success/error message — same pattern as the existing `process_signup.php`.
- New `tab=volunteers` view in `admin.php`, guarded by the existing `admin_check.php` session check. Includes:
  - List/search/filter of applicants
  - POST handlers for `approve_volunteer`, `decline_volunteer`, `delete_volunteer`

## Test

- Form rejects submission when required fields are missing or invalid.
- Non-logged-in users cannot reach `admin.php?tab=volunteers`.
- Approve/decline/delete actions correctly update `status` or remove the row.

## Deploy

- Ships with the rest of the site to the existing hosting server — no separate deployment step required.

## Monitor

- Admin checks the Pending queue periodically.
- No automated monitoring required at this project's scale.

## Notes

- No new authentication work needed — reuses the existing session-based admin login already built for the news/events/reports tabs.
- `volunteer.html`'s current JS only shows a popup and does not send data anywhere yet (placeholder comment references Firebase) — this needs to be changed to a real form POST to `process_volunteer.php`.
