
<?php
declare(strict_types=1);

// Connect to the TLF Connect MySQL database.
function getDbConnection(): PDO
{
    // Read database settings securely.
    $host = getenv('DB_HOST') ?: '';
    $port = getenv('DB_PORT') ?: '3306';
    $name = getenv('DB_NAME') ?: '';
    $user = getenv('DB_USER') ?: '';
    $password = getenv('DB_PASS');

    if ($host === '' || $name === '' ||
        $user === '' || $password === false) {
        throw new RuntimeException(
            'Database configuration is missing.'
        );
    }

    // Prepare the MySQL connection.
    $dsn = "mysql:host=$host;port=$port;dbname=$name;charset=utf8mb4";

    // Create a secure connection using PDO.
    return new PDO($dsn, $user, $password, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false
    ]);
}
