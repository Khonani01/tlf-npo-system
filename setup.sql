-- Run in phpMyAdmin: click the "SQL" tab, paste everything, click Go.
-- Creates the database and the tables the website needs so far.
-- NOTE: the table/column names here are my proposal - check them against the
-- structure Khumbelo agrees for tlf_db so the website, app and API all match.

CREATE DATABASE IF NOT EXISTS tlf_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tlf_db;

-- People who can log in (admins, volunteers, donors, stakeholders)
CREATE TABLE IF NOT EXISTS users (
    userID        INT AUTO_INCREMENT PRIMARY KEY,
    fullName      VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    passwordHash  VARCHAR(255) NULL,          -- password_hash() result; empty for Google-only accounts
    firebaseUID   VARCHAR(128) NULL UNIQUE,   -- filled in when someone signs in with Google
    role          ENUM('admin','volunteer','donor','stakeholder') NOT NULL DEFAULT 'volunteer',
    phone         VARCHAR(20) NULL,
    createdAt     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Volunteer applications from volunteer.html (reviewed in the admin Volunteers tab)
CREATE TABLE IF NOT EXISTS volunteers (
    volunteerID  INT AUTO_INCREMENT PRIMARY KEY,
    fullName     VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    skill        VARCHAR(50)  NOT NULL,
    eventChoice  VARCHAR(50)  NOT NULL,
    message      TEXT NULL,
    status       ENUM('pending','approved','declined') NOT NULL DEFAULT 'pending',
    submittedAt  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewedBy   INT NULL,
    UNIQUE KEY uq_email_event (email, eventChoice),   -- no applying twice for the same event
    CONSTRAINT fk_volunteers_reviewer
        FOREIGN KEY (reviewedBy) REFERENCES users(userID) ON DELETE SET NULL
) ENGINE=InnoDB;
