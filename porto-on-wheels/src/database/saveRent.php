<?php

include("../../src/database/conn.php");
$user_id = $_POST["userID"];
$date = $_POST["rentDate"];
$json = json_decode($_POST["products"], true);

if (isset($user_id) && isset($date)) {
    for ($i = 0; $i < count($json); $i++) {
        $rents = $db->query("INSERT INTO rents(id, product_id, account_id, date, quantity, plan)
         values(null, ?,?,?,?,?)", $json[$i]["id"], $user_id, $date, $json[$i]["count"], $json[$i]["plan"]);
        header("Location: ../../index.php");
    }
} else {
    header("Location: ../../index.php");
}


include("../../src/database/close.php");
