<?php
// db_connect.php - opens ONE connection to MySQL, named $conn.
// Every page that needs the database starts with:  require_once "db_connect.php";

// Use the private settings file if it exists (the safe way, used when we deploy).
// If it doesn't, fall back to the standard XAMPP settings so local testing still works.
if (file_exists(__DIR__ . "/db_config.php")) {
    require_once __DIR__ . "/db_config.php";
} else {
    define('DB_HOST', 'localhost');
    define('DB_USER', 'root');
    define('DB_PASS', '');
    define('DB_NAME', 'tlf_db');
}

// Don't let PHP print raw database errors to visitors (they can reveal secrets)
mysqli_report(MYSQLI_REPORT_OFF);

$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

if ($conn->connect_error) {
    // The real reason goes to the server's error log; the visitor sees a friendly message
    error_log("Database connection failed: " . $conn->connect_error);
    http_response_code(500);
    die("Sorry, the service is temporarily unavailable. Please try again later.");
}

// utf8mb4 so names with accents and emoji save correctly
$conn->set_charset("utf8mb4");