<?php

// TLF Connect - User Registration API
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');

// Return JSON with an appropriate HTTP status.
function sendResponse(int $status, array $data): void {
    http_response_code($status);
    echo json_encode($data);
    exit;
}

// Only accept POST requests.
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Allow: POST');
    sendResponse(405, [
        'success' => false,
        'message' => 'Only POST requests are allowed.'
    ]);
}

// Read incoming JSON.
if (stripos($_SERVER['CONTENT_TYPE'] ?? '', 'application/json') !== 0) {
    sendResponse(415, [
        'success' => false,
        'message' => 'Content-Type must be application/json.'
    ]);
}

$input = json_decode(file_get_contents('php://input'), true);

if (!is_array($input)) {
    sendResponse(400, [
        'success' => false,
        'message' => 'Invalid JSON data.'
    ]);
}

// Validate input types.
foreach (['full_name', 'email', 'password'] as $field) {
    if (!isset($input[$field]) || !is_string($input[$field])) {
        sendResponse(422, [
            'success' => false,
            'message' => "Valid $field is required."
        ]);
    }
}

$name = trim($input['full_name']);
$email = strtolower(trim($input['email']));
$password = $input['password'];

if ($name === '' || strlen($name) > 100) {
    sendResponse(422, [
        'success' => false,
        'message' => 'Name must be between 1 and 100 characters.'
    ]);
}

if (strlen($email) > 100 || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendResponse(422, [
        'success' => false,
        'message' => 'Enter a valid email address.'
    ]);
}

if (
    strlen($password) < 8 ||
    strlen($password) > 72 ||
    !preg_match('/[A-Z]/', $password) ||
    !preg_match('/[a-z]/', $password) ||
    !preg_match('/[0-9]/', $password)
) {
    sendResponse(422, [
        'success' => false,
        'message' => 'Password must be 8-72 characters and include uppercase, lowercase and a number.'
    ]);
}

// Validate the optional phone number.
$phone = $input['phone_number'] ?? null;

if ($phone !== null) {
    if (!is_string($phone)) {
        sendResponse(422, [
            'success' => false,
            'message' => 'Invalid phone number.'
        ]);
    }

    $phone = trim($phone);

    if ($phone === '') {
        $phone = null;
    } else {
        $digits = preg_replace('/\D/', '', $phone);

        if (
            strlen($phone) > 20 ||
            !preg_match('/^\+?[0-9 ()-]+$/', $phone) ||
            strlen($digits) < 9 ||
            strlen($digits) > 15
        ) {
            sendResponse(422, [
                'success' => false,
                'message' => 'Enter a valid phone number.'
            ]);
        }
    }
}

try {
    require_once __DIR__ . '/db.php';
    $db = getDbConnection();

    // Hash the password before storing it.
    $hash = password_hash($password, PASSWORD_DEFAULT);

    // Prepared statements prevent SQL injection.
    $stmt = $db->prepare(
        'INSERT INTO `Users`
         (`full_name`, `email`, `password_hash`, `phone_number`)
         VALUES (?, ?, ?, ?)'
    );

    $stmt->execute([$name, $email, $hash, $phone]);

    sendResponse(201, [
        'success' => true,
        'message' => 'Registration successful.',
        'user' => [
            'user_id' => (int) $db->lastInsertId(),
            'full_name' => $name,
            'email' => $email
        ]
    ]);

} catch (PDOException $e) {
    // MySQL error 1062 means a duplicate unique value.
    if ((int) ($e->errorInfo[1] ?? 0) === 1062) {
        sendResponse(409, [
            'success' => false,
            'message' => 'Email is already registered.'
        ]);
    }

    error_log($e->getMessage());

    sendResponse(500, [
        'success' => false,
        'message' => 'Registration could not be completed.'
    ]);

} catch (Throwable $e) {
    error_log($e->getMessage());

    sendResponse(500, [
        'success' => false,
        'message' => 'Registration could not be completed.'
    ]);
}
