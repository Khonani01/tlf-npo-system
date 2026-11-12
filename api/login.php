<?php

// TLF Connect - Login API
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');

// Return a JSON response.
function loginResponse(int $status, array $data): void {
    http_response_code($status);
    echo json_encode($data);
    exit;
}

// Only allow POST requests.
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Allow: POST');
    loginResponse(405, [
        'success' => false,
        'message' => 'Only POST requests are allowed.'
    ]);
}

// Require JSON input.
$contentType = strtolower(trim(
    explode(';', $_SERVER['CONTENT_TYPE'] ?? '')[0]
));

if ($contentType !== 'application/json') {
    loginResponse(415, [
        'success' => false,
        'message' => 'Content-Type must be application/json.'
    ]);
}

// Read login details.
$input = json_decode(file_get_contents('php://input'), true);

if (!is_array($input)) {
    loginResponse(400, [
        'success' => false,
        'message' => 'Invalid JSON data.'
    ]);
}

$email = $input['email'] ?? null;
$password = $input['password'] ?? null;

// Validate the email and password.
if (
    !is_string($email) ||
    !is_string($password) ||
    trim($email) === '' ||
    $password === ''
) {
    loginResponse(422, [
        'success' => false,
        'message' => 'Email and password are required.'
    ]);
}

$email = strtolower(trim($email));

if (
    strlen($email) > 100 ||
    !filter_var($email, FILTER_VALIDATE_EMAIL)
) {
    loginResponse(422, [
        'success' => false,
        'message' => 'Enter a valid email address.'
    ]);
}

if (strlen($password) > 1024) {
    loginResponse(422, [
        'success' => false,
        'message' => 'Invalid password input.'
    ]);
}

try {
    // Connect to the MySQL database.
    require_once __DIR__ . '/db.php';
    $db = getDbConnection();

    // Find the user by email.
    $stmt = $db->prepare(
        'SELECT user_id, full_name, email,
                password_hash, role
         FROM `Users`
         WHERE email = ?
         LIMIT 1'
    );

    $stmt->execute([$email]);
    $user = $stmt->fetch();

    // Verify the password against the stored hash.
    if (
        !$user ||
        !password_verify($password, $user['password_hash'])
    ) {
        loginResponse(401, [
            'success' => false,
            'message' => 'Invalid email or password.'
        ]);
    }

    // Create a login session after verifying the password.
    ini_set('session.use_strict_mode', '1');

    session_name('TLFSESSID');

    session_set_cookie_params([
        'lifetime' => 0,
        'path' => '/',
        'secure' => !empty($_SERVER['HTTPS'])
            && $_SERVER['HTTPS'] !== 'off',
        'httponly' => true,
        'samesite' => 'Lax'
    ]);

    if (!session_start()) {
        loginResponse(500, [
            'success' => false,
            'message' => 'Unable to start login session.'
        ]);
    }

    // Prevent session fixation.
    session_regenerate_id(true);

    // Remember the authenticated user.
    $_SESSION['user_id'] = (int) $user['user_id'];

    // Credentials are correct.
    loginResponse(200, [
        'success' => true,
        'message' => 'Login credentials verified.',
        'user' => [
            'user_id' => (int) $user['user_id'],
            'full_name' => $user['full_name'],
            'email' => $user['email'],
            'role' => $user['role']
        ]
    ]);

} catch (Throwable $e) {
    error_log($e->getMessage());

    loginResponse(500, [
        'success' => false,
        'message' => 'Login could not be completed.'
    ]);
}
