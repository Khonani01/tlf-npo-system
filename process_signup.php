<?php
// process_signup.php - creates a new account from signup.html.
// The password is turned into a "hash" (a scrambled string) and only the hash is saved.
require_once "db_connect.php";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    header("Location: signup.html");
    exit;
}

$fullName = trim($_POST["fullName"] ?? "");
$email    = trim($_POST["email"] ?? "");
$phone    = trim($_POST["phone"] ?? "");
$role     = $_POST["role"] ?? "";
$password = $_POST["password"] ?? "";
$confirm  = $_POST["confirm"] ?? "";
$terms    = isset($_POST["terms"]);

// Visitors can only pick these roles. "admin" is NOT on the list, so nobody can
// make themselves an admin by editing the form.
$allowedRoles = ["volunteer", "donor", "stakeholder"];

if ($fullName === "" || $email === "" || $role === "" || $password === "") {
    header("Location: signup.html?error=missing"); exit;
}
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    header("Location: signup.html?error=email"); exit;
}
// Phone is optional, but if given it must look like a phone number
if ($phone !== "" && !preg_match('/^\+?[0-9 ()-]{9,20}$/', $phone)) {
    header("Location: signup.html?error=phone"); exit;
}
if (!in_array($role, $allowedRoles, true)) {
    header("Location: signup.html?error=role"); exit;
}
// Strong enough: 8+ characters with at least one letter and one number
if (strlen($password) < 8 || !preg_match('/[A-Za-z]/', $password) || !preg_match('/[0-9]/', $password)) {
    header("Location: signup.html?error=weak"); exit;
}
if ($password !== $confirm) {
    header("Location: signup.html?error=mismatch"); exit;
}
if (!$terms) {
    header("Location: signup.html?error=terms"); exit;
}
$fullName = mb_substr($fullName, 0, 100);
$email    = mb_substr($email, 0, 100);
$phoneOrNull = ($phone === "") ? null : $phone;

// Is that email already registered?
$check = $conn->prepare("SELECT userID FROM users WHERE email = ?");
$check->bind_param("s", $email);
$check->execute();
$check->store_result();
if ($check->num_rows > 0) {
    header("Location: signup.html?error=exists"); exit;
}
$check->close();

$hash = password_hash($password, PASSWORD_DEFAULT);
$stmt = $conn->prepare("INSERT INTO users (fullName, email, passwordHash, role, phone) VALUES (?, ?, ?, ?, ?)");
$stmt->bind_param("sssss", $fullName, $email, $hash, $role, $phoneOrNull);

if ($stmt->execute()) {
    header("Location: signup.html?success=1");
} else {
    error_log("Signup insert failed: " . $stmt->error);
    header("Location: signup.html?error=db");
}
exit;
