<?php

// TLF Connect - Donations API
// Retrieve existing donation records for the logged-in user.

header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');

function donationResponse(int $status, array $data): void {
    http_response_code($status);
    echo json_encode($data);
    exit;
}

// Only allow GET requests for donation history.
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    donationResponse(405, [
        'success' => false,
        'message' => 'Only GET requests are allowed.'
    ]);
}

// Check the user's login session.
session_name('TLFSESSID');

if (!session_start()) {
    donationResponse(500, [
        'success' => false,
        'message' => 'Unable to start session.'
    ]);
}

$userId = $_SESSION['user_id'] ?? null;

if (!is_int($userId) || $userId < 1) {
    donationResponse(401, [
        'success' => false,
        'message' => 'Please log in to view your donations.'
    ]);
}

try {
    // Connect to MySQL.
    require_once __DIR__ . '/db.php';
    $db = getDbConnection();

    // Retrieve donations belonging to this user.
    $stmt = $db->prepare(
        'SELECT donation_id, amount, payment_method,
                donation_date
         FROM `Donations`
         WHERE user_id = ?
         ORDER BY donation_date DESC, donation_id DESC
         LIMIT 50'
    );

    $stmt->execute([$userId]);

    $donations = $stmt->fetchAll();

    donationResponse(200, [
        'success' => true,
        'message' => 'Donation records retrieved successfully.',
        'data' => $donations
    ]);

} catch (Throwable $e) {
    error_log($e->getMessage());

    donationResponse(500, [
        'success' => false,
        'message' => 'Unable to retrieve donation records.'
    ]);
}
