<?php

include("../../src/database/conn.php");
// echo $_POST["quantity"];
// echo $_POST["name"] . "<br>";
// echo file_get_contents($_FILES['image']['tmp_name']) . "<br>";
if (isset($_POST["new_name"]) && isset($_POST["new_email"])) {
    $name = $_POST["new_name"];
    $phone_number = $_POST["new_phone_number"];
    $email = $_POST["new_email"];
    $id = $_POST["new_id"];
    $file = $_POST["new_image"];
    $file = file_get_contents($_FILES["new_image"]["tmp_name"]);

    $ola = $db->query("update accounts set name=?, phone_number=?, email=?, profile_img=? where id=?", $name, $phone_number, $email, $file, $id);
    session_destroy();
    header("Location: ../../index.php");
} else {
    header("Location: ../../index.php");
}


include("../../src/database/close.php");
