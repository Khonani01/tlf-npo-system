-- TLF Connect - clean MySQL/MariaDB schema for automated CI tests
-- Creates tables only in the database selected by the CI runner.
-- Do NOT run against a live database or one with existing tables/data.
-- CI setup must create and select an empty, temporary test database first.

CREATE TABLE `Users` (
    `user_id` INT NOT NULL AUTO_INCREMENT,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `phone_number` VARCHAR(20) NULL,
    `role` VARCHAR(20) DEFAULT 'User',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uq_users_email` (`email`),
    CONSTRAINT `chk_users_role` CHECK (`role` IN ('User', 'Admin', 'Volunteer'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `NewsEvents` (
    `event_id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(150) NOT NULL,
    `description` TEXT NOT NULL,
    `event_date` DATE NOT NULL,
    `location` VARCHAR(150) NULL,
    `category` VARCHAR(50) NULL,
    `image_url` VARCHAR(255) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`event_id`),
    CONSTRAINT `chk_newsevents_category` CHECK (`category` IN ('News', 'Event', 'Announcement'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Volunteers` (
    `volunteer_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `skills` VARCHAR(255) NULL,
    `availability` VARCHAR(100) NULL,
    `status` VARCHAR(20) DEFAULT 'Pending',
    `application_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`volunteer_id`),
    CONSTRAINT `chk_volunteers_status` CHECK (`status` IN ('Pending', 'Approved', 'Rejected')),
    CONSTRAINT `fk_volunteer_user` FOREIGN KEY (`user_id`)
        REFERENCES `Users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Donations` (
    `donation_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `payment_method` VARCHAR(50) DEFAULT 'Card',
    `transaction_ref` VARCHAR(100) NULL,
    `donation_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`donation_id`),
    UNIQUE KEY `uq_donations_transaction_ref` (`transaction_ref`),
    CONSTRAINT `chk_donations_amount` CHECK (`amount` > 0),
    CONSTRAINT `fk_donation_user` FOREIGN KEY (`user_id`)
        REFERENCES `Users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ImpactReports` (
    `report_id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(150) NOT NULL,
    `summary` TEXT NOT NULL,
    `report_year` INT NOT NULL,
    `document_url` VARCHAR(255) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
