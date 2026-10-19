<?php
// process_volunteer.php
// Receives the volunteer.html form, checks it on the server, saves it to the
// volunteers table, then sends the visitor back with a success or error code.
// Same pattern as process_signup.php.
require_once "db_connect.php";

// Only accept form submissions (not someone typing the address into the browser)
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    header("Location: volunteer.html");
    exit;
}

// 1. Read the values the form sent. trim() removes spaces at the start/end.
$fullName    = trim($_POST["fullName"]    ?? "");
$email       = trim($_POST["email"]       ?? "");
$phone       = trim($_POST["phone"]       ?? "");
$skill       = trim($_POST["skill"]       ?? "");
$eventChoice = trim($_POST["eventChoice"] ?? "");
$message     = trim($_POST["message"]     ?? "");
$terms       = isset($_POST["terms"]);

// The only values we accept from the two dropdowns.
// (Someone could fake a form, so we never trust what the browser sends.)
$allowedSkills = ["teaching","mentoring","events","admin","health",
                  "construction","communications","it","other"];
$allowedEvents = ["summit","cleanup","mentorship","general"];

// 2. Validate. Each check sends the visitor back with a specific error code.
if ($fullName === "" || $email === "" || $phone === "" || $skill === "" || $eventChoice === "") {
    header("Location: volunteer.html?error=missing"); exit;
}
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    header("Location: volunteer.html?error=email"); exit;
}
// Phone: optional +, then digits/spaces/brackets/dashes, 9-20 characters
if (!preg_match('/^\+?[0-9 ()-]{9,20}$/', $phone)) {
    header("Location: volunteer.html?error=phone"); exit;
}
if (!in_array($skill, $allowedSkills, true) || !in_array($eventChoice, $allowedEvents, true)) {
    header("Location: volunteer.html?error=choice"); exit;
}
if (!$terms) {
    header("Location: volunteer.html?error=terms"); exit;
}
// Keep lengths within what the database columns can hold
$fullName = mb_substr($fullName, 0, 100);
$email    = mb_substr($email, 0, 100);
$message  = mb_substr($message, 0, 500);
// An empty message is saved as NULL (the column allows "nothing")
$messageOrNull = ($message === "") ? null : $message;

// 3. Already applied for this event with this email?
$check = $conn->prepare("SELECT volunteerID FROM volunteers WHERE email = ? AND eventChoice = ?");
$check->bind_param("ss", $email, $eventChoice);
$check->execute();
$check->store_result();
if ($check->num_rows > 0) {
    header("Location: volunteer.html?error=duplicate"); exit;
}
$check->close();

// 4. Save. The ? marks are filled in safely (prepared statement), which
//    stops SQL injection. status and submittedAt fill themselves in.
$stmt = $conn->prepare(
    "INSERT INTO volunteers (fullName, email, phone, skill, eventChoice, message)
     VALUES (?, ?, ?, ?, ?, ?)"
);
$stmt->bind_param("ssssss", $fullName, $email, $phone, $skill, $eventChoice, $messageOrNull);

if ($stmt->execute()) {
    header("Location: volunteer.html?success=1");
} else {
    // Write the real reason to the server log; show the visitor a friendly message
    error_log("Volunteer insert failed: " . $stmt->error);
    header("Location: volunteer.html?error=db");
}
exit;
?>
