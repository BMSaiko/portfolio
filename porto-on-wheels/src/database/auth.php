<?php
include("conn.php");

// TODO: Verificar se o email já existe ou não etc ao fazer registo
$email = $_POST["email"];
$password = $_POST["password"];
$name = $_POST["name"];
$phone_number = $_POST["phone_number"];

if (isset($email) && isset($password)) {
    if (isset($name)) {
        $insert = $db->query('INSERT INTO accounts (name,email,password,phone_number) VALUES (?,?,?,?)', $name, $email, $email, $phone_number);
        $account = $db->query('SELECT * FROM accounts WHERE email=? AND password=?', $email, $password)->fetchArray();
        if ($account) {
            session_start();
            $_SESSION['sid'] = session_id();
            $_SESSION['id'] = $account["id"];
            $_SESSION['role'] = $account["role"];
            $_SESSION['name'] = $account["name"];
            $_SESSION['profile_image'] = $account["profile_img"];
            $_SESSION['phone_number'] = $account["phone_number"];
            $_SESSION['email'] = $account["email"];
            header("Location: ../../index.php");
        } else {
            header("Location: ../../login.php");
        }
    } else {
        $account = $db->query('SELECT * FROM accounts WHERE email=? AND password=?', $email, $password)->fetchArray();
        if ($account) {
            session_start();
            $_SESSION['sid'] = session_id();
            $_SESSION['id'] = $account["id"];
            $_SESSION['role'] = $account["role"];
            $_SESSION['name'] = $account["name"];
            $_SESSION['phone_number'] = $account["phone_number"];
            $_SESSION['email'] = $account["email"];
            $_SESSION['profile_image'] = $account["profile_img"];
            if (isset($_POST["login"])) {
                if ($account["role"] == "ADMIN") {
                    header("Location: ../../admin/index.php");
                } else {
                    header("Location: ../../index.php");
                }
            } else {
                header("Location: ../../index.php");
            }
        } else {
            header("Location: ../../index.php");
        }
    }
}

include("close.php");
