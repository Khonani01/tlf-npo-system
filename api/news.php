
<?php

// TLF Connect - News and Events API
header('Content-Type: application/json; charset=utf-8');

// Only allow GET requests.
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    http_response_code(405);

    echo json_encode([
        'success' => false,
        'message' => 'Only GET requests are allowed.'
    ]);
    exit;
}

try {
    // Connect to the database.
    require_once __DIR__ . '/db.php';
    $db = getDbConnection();

    // Retrieve the latest 50 news and events records.
    $stmt = $db->query(
        'SELECT event_id, title, description,
                event_date, location, category,
                image_url, created_at
         FROM `NewsEvents`
         ORDER BY event_date DESC, event_id DESC
         LIMIT 50'
    );

    $items = $stmt->fetchAll();

    // Return the records as JSON.
    http_response_code(200);

    echo json_encode([
        'success' => true,
        'message' => 'News and events retrieved successfully.',
        'data' => $items
    ]);

} catch (Throwable $e) {
    // Log the technical error privately.
    error_log($e->getMessage());

    http_response_code(500);

    echo json_encode([
        'success' => false,
        'message' => 'Unable to retrieve news and events.'
    ]);
}
