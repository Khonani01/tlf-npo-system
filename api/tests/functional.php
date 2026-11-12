
<?php

// TLF Connect - Functional Registration and Login Tests
// Run only against a temporary local test database.

if (PHP_SAPI !== 'cli') {
    http_response_code(403);
    exit;
}

$baseUrl = rtrim(
    getenv('API_BASE_URL') ?: 'http://127.0.0.1:8000',
    '/'
);

// Prevent accidentally testing against the live website.
$host = parse_url($baseUrl, PHP_URL_HOST);

if (!in_array($host, ['127.0.0.1', 'localhost'], true)) {
    fwrite(STDERR, "Use a local test API only.\n");
    exit(2);
}

// Send a POST request and read its JSON response.
function sendTestRequest(
    string $baseUrl,
    string $path,
    array $payload
): array {
    $context = stream_context_create([
        'http' => [
            'method' => 'POST',
            'header' => "Content-Type: application/json\r\n",
            'content' => json_encode($payload),
            'ignore_errors' => true,
            'timeout' => 10
        ]
    ]);

    $response = @file_get_contents(
        "$baseUrl/$path",
        false,
        $context
    );

    $headers = $http_response_header ?? [];
    $status = 0;

    if (
        isset($headers[0]) &&
        preg_match('/\s(\d{3})(?:\s|$)/', $headers[0], $matches)
    ) {
        $status = (int) $matches[1];
    }

    $data = is_string($response)
        ? json_decode($response, true)
        : null;

    return [$status, $data];
}

// Generate a unique test email for each run.
$email = 'tlf-test-' . bin2hex(random_bytes(6)) . '@example.com';
$password = 'TestPassword123!';

$validUser = [
    'full_name' => 'TLF Automated Tester',
    'email' => $email,
    'password' => $password
];

$tests = [
    [
        'Invalid registration email',
        'register.php',
        array_merge($validUser, ['email' => 'invalid-email']),
        422,
        false
    ],
    [
        'Weak registration password',
        'register.php',
        array_merge($validUser, ['password' => 'weak']),
        422,
        false
    ],
    [
        'Valid user registration',
        'register.php',
        $validUser,
        201,
        true
    ],
    [
        'Duplicate registration',
        'register.php',
        $validUser,
        409,
        false
    ],
    [
        'Incorrect login password',
        'login.php',
        ['email' => $email, 'password' => 'WrongPassword123!'],
        401,
        false
    ],
    [
        'Correct user login',
        'login.php',
        ['email' => $email, 'password' => $password],
        200,
        true
    ]
];

$passed = 0;
$failed = 0;

foreach ($tests as $test) {
    [$name, $path, $payload, $expectedStatus, $expectedSuccess] = $test;

    [$status, $data] = sendTestRequest(
        $baseUrl,
        $path,
        $payload
    );

    $success = is_array($data)
        ? ($data['success'] ?? null)
        : null;

    $validResponse = $status === $expectedStatus
        && $success === $expectedSuccess;

    // Confirm successful requests return the correct account.
    if ($validResponse && $expectedSuccess) {
        $validResponse = ($data['user']['email'] ?? null) === $email;
    }

    if ($validResponse) {
        echo "PASS: $name\n";
        $passed++;
    } else {
        echo "FAIL: $name (HTTP $status)\n";
        $failed++;
    }
}

 // Verify that the registered user's password is hashed.
try {
    require_once dirname(__DIR__) . '/db.php';
    $db = getDbConnection();

    $stmt = $db->prepare(
        'SELECT password_hash FROM `Users`
         WHERE email = ? LIMIT 1'
    );
    $stmt->execute([$email]);

    $storedHash = $stmt->fetchColumn();

    if (
        is_string($storedHash) &&
        $storedHash !== $password &&
        password_verify($password, $storedHash)
    ) {
        echo "PASS: Password stored securely\n";
        $passed++;
    } else {
        echo "FAIL: Password storage verification\n";
        $failed++;
    }
} catch (Throwable $e) {
    echo "FAIL: Password storage verification\n";
    $failed++;
}

echo "\nFunctional tests passed: $passed\n";
echo "Functional tests failed: $failed\n";

exit($failed > 0 ? 1 : 0);
