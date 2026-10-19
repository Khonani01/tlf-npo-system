<?php
// admin_check.php - the "bouncer". Put  require_once "admin_check.php";  at the TOP
// of any page only admins may see. If you are not a logged-in admin, you are sent to the login page.
session_start();

if (empty($_SESSION["userID"]) || ($_SESSION["role"] ?? "") !== "admin") {
    header("Location: login.html");
    exit;
}
