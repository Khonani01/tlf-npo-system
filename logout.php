<?php
// logout.php - ends the session so the browser is no longer "logged in".
session_start();
$_SESSION = [];            // forget everything stored in the session
session_destroy();         // delete the session itself
header("Location: login.html");
exit;
