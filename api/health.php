
<?php
declare(strict_types=1);

// Tell the client that this API returns JSON.
header('Content-Type: application/json; charset=utf-8');

// Prevent the response from being cached.
header('Cache-Control: no-store');

// Return a successful HTTP response.
http_response_code(200);

// Send a JSON response to the client.
echo json_encode([
    'success' => true,
    'message' => 'TLF Connect API is running'
]);
