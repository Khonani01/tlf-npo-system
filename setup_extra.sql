-- Run in phpMyAdmin (SQL tab) AFTER setup.sql / setup_hosting.sql.
-- Adds the tables for the contact form, news/events and reports.

CREATE TABLE IF NOT EXISTS contact_messages (
    messageID   INT AUTO_INCREMENT PRIMARY KEY,
    fullName    VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL,
    subject     VARCHAR(150) NULL,
    message     TEXT NOT NULL,
    sentAt      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- News articles and events share one table; "type" says which it is
CREATE TABLE IF NOT EXISTS news_events (
    itemID      INT AUTO_INCREMENT PRIMARY KEY,
    type        ENUM('news','event') NOT NULL,
    title       VARCHAR(255) NOT NULL,
    content     TEXT NOT NULL,
    eventDate   DATE NULL,            -- events: the day it happens; news: the publish date
    eventTime   VARCHAR(20) NULL,     -- events only, e.g. 09:00 AM
    location    VARCHAR(150) NULL,    -- events only
    imagePath   VARCHAR(255) NULL,
    createdAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reports (
    reportID    INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description VARCHAR(500) NULL,
    filePath    VARCHAR(255) NOT NULL,   -- e.g. annual-report-2024.pdf
    createdAt   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Starter content (from the existing site) so the pages are not empty
INSERT INTO news_events (type, title, content, eventDate, imagePath) VALUES
('news','Youth Leadership Workshop','The Tshwane Leadership Foundation hosted a youth leadership workshop focused on empowering young people with leadership and life skills.','2026-03-15','images/event1.jpeg'),
('news','Community Development Program','Volunteers and community members gathered to support local development projects aimed at improving livelihoods in Tshwane communities.','2026-03-10','images/event2.jpeg'),
('news','Education Support Initiative','The foundation continues to support learners through mentorship, educational programs, and access to learning resources.','2026-03-05','images/event3.jpeg'),
('event','Youth Leadership Summit 2026','A full-day summit bringing together young leaders from across Tshwane for workshops and networking.','2026-12-12',NULL);
UPDATE news_events SET eventTime='09:00 AM', location='TLF Community Hall', imagePath='images/event1.jpeg' WHERE type='event';

INSERT INTO reports (title, description, filePath) VALUES
('Annual Report 2024','Overview of the foundation''s activities, programs, and achievements throughout 2024.','annual-report-2024.pdf'),
('Community Impact Report','Highlights the positive impact our projects have made in communities across Tshwane.','community-impact.pdf'),
('Financial Transparency Report','Details about funding, spending, and how donations support community development initiatives.','financial-report.pdf');
