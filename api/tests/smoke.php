<?php

// TLF Connect - Automated API Smoke Tests
// Run from the command line, not the browser.

if (PHP_SAPI !== 'cli') {
    http_response_code(403);
    exit;
}

$baseUrl = rtrim(
    getenv('API_BASE_URL') ?: 'http://localhost/tlf-connect/api',
    '/'
);

// Test name, endpoint, HTTP method, expected status, expected success, optional JSON body.
$tests = [
    ['Health check', 'health.php', 'GET', 200, true],
    ['Register rejects GET', 'register.php', 'GET', 405, false],
    ['Login rejects GET', 'login.php', 'GET', 405, false],
    ['News rejects POST', 'news.php', 'POST', 405, false],
    ['Volunteers reject GET', 'volunteers.php', 'GET', 405, false],
    ['Donations reject POST', 'donations.php', 'POST', 405, false],
    ['News GET succeeds', 'news.php', 'GET', 200, true],
    ['Donations require login', 'donations.php', 'GET', 401, false],
    ['Register rejects missing fields', 'register.php', 'POST', 422, false, '{}'],
    ['Login rejects missing fields', 'login.php', 'POST', 422, false, '{}']
];

$passed = 0;
$failed = 0;

foreach ($tests as $test) {
    [$name, $path, $method, $expectedStatus, $expectedSuccess] = $test;
    $body = $test[5] ?? null;

    $options = [
        'method' => $method,
        'ignore_errors' => true,
        'timeout' => 10
    ];

    if ($body !== null) {
        $options['header'] = "Content-Type: application/json\r\n";
        $options['content'] = $body;
    }

    $context = stream_context_create(['http' => $options]);
    $response = @file_get_contents("$baseUrl/$path", false, $context);
    $headers = $http_response_header ?? [];
    $status = 0;

    if (isset($headers[0]) && preg_match('/\s(\d{3})(?:\s|$)/', $headers[0], $matches)) {
        $status = (int) $matches[1];
    }

    $data = is_string($response) ? json_decode($response, true) : null;
    $success = is_array($data) ? ($data['success'] ?? null) : null;

    if ($status === $expectedStatus && $success === $expectedSuccess) {
        echo "PASS: $name\n";
        $passed++;
    } else {
        echo "FAIL: $name (expected HTTP $expectedStatus, got $status)\n";
        $failed++;
    }
}

echo "\nTests passed: $passed\n";
echo "Tests failed: $failed\n";

exit($failed > 0 ? 1 : 0);
