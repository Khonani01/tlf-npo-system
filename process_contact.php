<?php
// process_contact.php - saves a message from contact.html into the contact_messages table.
require_once "db_connect.php";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    header("Location: contact.html");
    exit;
}

$fullName = trim($_POST["fullName"] ?? "");
$email    = trim($_POST["email"] ?? "");
$subject  = trim($_POST["subject"] ?? "");
$message  = trim($_POST["message"] ?? "");

if ($fullName === "" || $email === "" || $message === "") {
    header("Location: contact.html?error=missing"); exit;
}
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    header("Location: contact.html?error=email"); exit;
}
if (mb_strlen($message) < 10) {
    header("Location: contact.html?error=short"); exit;
}
$fullName = mb_substr($fullName, 0, 100);
$email    = mb_substr($email, 0, 100);
$subject  = mb_substr($subject, 0, 150);
$message  = mb_substr($message, 0, 2000);
$subjectOrNull = ($subject === "") ? null : $subject;

$stmt = $conn->prepare("INSERT INTO contact_messages (fullName, email, subject, message) VALUES (?, ?, ?, ?)");
$stmt->bind_param("ssss", $fullName, $email, $subjectOrNull, $message);

if ($stmt->execute()) {
    header("Location: contact.html?success=1");
} else {
    error_log("Contact insert failed: " . $stmt->error);
    header("Location: contact.html?error=db");
}
exit;
