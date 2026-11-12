<?php

// TLF Connect - Volunteer Registration API
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');

function volunteerResponse(int $status, array $data): void {
    http_response_code($status);
    echo json_encode($data);
    exit;
}

// Volunteer applications require POST.
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Allow: POST');
    volunteerResponse(405, [
        'success' => false,
        'message' => 'Only POST requests are allowed.'
    ]);
}

// Require JSON.
$contentType = strtolower(trim(
    explode(';', $_SERVER['CONTENT_TYPE'] ?? '')[0]
));

if ($contentType !== 'application/json') {
    volunteerResponse(415, [
        'success' => false,
        'message' => 'Content-Type must be application/json.'
    ]);
}

// The user must be authenticated.
session_name('TLFSESSID');

if (!session_start()) {
    volunteerResponse(500, [
        'success' => false,
        'message' => 'Unable to start a session.'
    ]);
}

$userId = $_SESSION['user_id'] ?? null;

if (!is_int($userId) || $userId < 1) {
    volunteerResponse(401, [
        'success' => false,
        'message' => 'Please log in before applying.'
    ]);
}

// Read the incoming application.
$input = json_decode(file_get_contents('php://input'), true);

if (!is_array($input)) {
    volunteerResponse(400, [
        'success' => false,
        'message' => 'Invalid JSON data.'
    ]);
}

$skills = $input['skills'] ?? null;
$availability = $input['availability'] ?? null;

// Validate skills.
if (
    !is_string($skills) ||
    trim($skills) === '' ||
    strlen(trim($skills)) > 255
) {
    volunteerResponse(422, [
        'success' => false,
        'message' => 'Enter valid skills (maximum 255 characters).'
    ]);
}

// Validate availability.
if (
    !is_string($availability) ||
    trim($availability) === '' ||
    strlen(trim($availability)) > 100
) {
    volunteerResponse(422, [
        'success' => false,
        'message' => 'Enter valid availability (maximum 100 characters).'
    ]);
}

$skills = trim($skills);
$availability = trim($availability);

try {
    require_once __DIR__ . '/db.php';
    $db = getDbConnection();

    // Save the volunteer application.
    // Status defaults to Pending in the database.
    $stmt = $db->prepare(
        'INSERT INTO `Volunteers`
         (`user_id`, `skills`, `availability`)
         VALUES (?, ?, ?)'
    );

    $stmt->execute([
        $userId,
        $skills,
        $availability
    ]);

    volunteerResponse(201, [
        'success' => true,
        'message' => 'Volunteer application submitted successfully.',
        'volunteer_id' => (int) $db->lastInsertId(),
        'status' => 'Pending'
    ]);

} catch (Throwable $e) {
    error_log($e->getMessage());

    volunteerResponse(500, [
        'success' => false,
        'message' => 'Unable to submit volunteer application.'
    ]);
}
