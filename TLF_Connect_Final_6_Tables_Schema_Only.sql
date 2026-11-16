-- TLF Connect / Task 3: Six-table schema (structure only)
-- Source: Khumbelo's final six-table database specification; converted from SQL Server to MySQL / MariaDB.
-- This file contains no passwords, account records, payment records, or personal data.
-- No DROP, DELETE, or ALTER operations. Do not import onto an existing database with similarly named tables.
-- To initialize an EMPTY MySQL/MariaDB database, select that database first and import this file.
-- The actual locally tested database had these six tables populated with demonstration records.

SET NAMES utf8mb4;

-- Create Khumbelo's exact six logical tables, converted to MySQL syntax.
-- 1. USERS TABLE (converted from SQL Server for MySQL/MariaDB)
CREATE TABLE `Users` (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NULL, 
    google_id VARCHAR(100) NULL, -- Nullable external account identifier from source
    avatar_url VARCHAR(255) NULL, 
    phone_number VARCHAR(20),
    role VARCHAR(20) DEFAULT 'User' CHECK (role IN ('User', 'Admin', 'Volunteer')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. NEWS & EVENTS TABLE
CREATE TABLE `NewsEvents` (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    event_date DATE NOT NULL,
    location VARCHAR(150),
    category VARCHAR(50) CHECK (category IN ('News', 'Event', 'Announcement')),
    image_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 3. VOLUNTEERS TABLE
CREATE TABLE `Volunteers` (
    volunteer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    skills VARCHAR(255),
    availability VARCHAR(100),
    status VARCHAR(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Approved', 'Rejected')),
    application_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_volunteer_user FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- 4. DONATIONS TABLE (Tracks EFT and Card donations)
CREATE TABLE `Donations` (
    donation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    payment_method VARCHAR(50) DEFAULT 'Card' CHECK (payment_method IN ('Card', 'EFT', 'Instant EFT')),
    transaction_ref VARCHAR(100) UNIQUE, 
    status VARCHAR(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Completed', 'Failed')),
    donation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_donation_user FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
);

-- 5. CONTACT MESSAGES TABLE
CREATE TABLE `ContactMessages` (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    subject VARCHAR(150),
    message_content TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'Unread' CHECK (status IN ('Unread', 'Read', 'Resolved')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 6. REPORTS TABLE
CREATE TABLE `ImpactReports` (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    summary TEXT NOT NULL,
    report_year INT NOT NULL,
    document_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
