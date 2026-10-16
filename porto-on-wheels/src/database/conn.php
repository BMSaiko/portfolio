<?php
include("db.php");
// Replace the variables with your database credentials
$dbhost = getenv('PORTOONWHEELS_DB_HOST') ?: 'localhost';
$dbuser = getenv('PORTOONWHEELS_DB_USER') ?: 'root';
$dbpass = getenv('PORTOONWHEELS_DB_PASSWORD') ?: '';
$dbname = "portoonwheels";

// Connect to the database
$db = new db($dbhost, $dbuser, $dbpass, $dbname);


// Check if the connection was successful
if (!$db) {
    die("Connection failed: " . mysqli_connect_error());
}
