<?php
// process_login.php - checks an admin's email and password, then starts a session.
// A "session" is how the server remembers you are logged in while you click around.
session_start();
require_once "db_connect.php";

// Only accept the login form being submitted
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    header("Location: login.html");
    exit;
}

$email    = trim($_POST["email"] ?? "");
$password = $_POST["password"] ?? "";      // never trim() passwords: spaces may be intentional

if ($email === "" || $password === "") {
    header("Location: login.html?error=missing");
    exit;
}

// Look the user up by email (prepared statement = safe from SQL injection)
$stmt = $conn->prepare("SELECT userID, fullName, passwordHash, role FROM users WHERE email = ?");
if (!$stmt) {
    error_log("Login query failed: " . $conn->error);
    header("Location: login.html?error=db");
    exit;
}
$stmt->bind_param("s", $email);
$stmt->execute();
$user = $stmt->get_result()->fetch_assoc();   // one row, or null if no such email
$stmt->close();

// Same message for "no such email" and "wrong password", so attackers can't
// use the page to find out which emails exist.
if (!$user || empty($user["passwordHash"]) || !password_verify($password, $user["passwordHash"])) {
    header("Location: login.html?error=invalid");
    exit;
}

// Correct password - but only admins may use this login
if ($user["role"] !== "admin") {
    header("Location: login.html?error=notadmin");
    exit;
}

// Success: start a fresh session ID (blocks "session fixation") and remember who this is
session_regenerate_id(true);
$_SESSION["userID"]   = (int)$user["userID"];
$_SESSION["fullName"] = $user["fullName"];
$_SESSION["role"]     = $user["role"];

header("Location: admin.php");
exit;
