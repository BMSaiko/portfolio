<?php

include("../../src/database/conn.php");
// echo $_POST["quantity"];
// echo $_POST["name"] . "<br>";
// echo file_get_contents($_FILES['image']['tmp_name']) . "<br>";

if (isset($_POST["table"])) {
    $table = $_POST["table"];
    switch ($table) {
        case "users":
            if (isset($_POST["name"]) && isset($_POST["phone"]) && isset($_POST["email"]) && isset($_POST["role"]) && isset($_POST["password"])) {
                $name = $_POST["name"];
                $phone = $_POST["phone"];
                $email = $_POST["email"];
                $role = strtoupper($_POST["role"]);
                $password = $_POST["password"];


                $accounts = $db->query("INSERT INTO accounts(id, name, phone_number, email, password, role) values(null, ?,?,?,?,?)", $name, $phone, $email, $password, $role);
                header("Location: ../tables_users.php");
            } else {
                echo "ADEUS";
            }
            break;

        case "products":
            if (isset($_POST["name"]) && isset($_POST["quantity"])) {
                $name = $_POST["name"];
                $quantity = $_POST["quantity"];

                if (isset($_POST["tipo"])) {
                    $tipo = $_POST["tipo"];
                    switch ($tipo) {
                        case "bikes":
                            if (isset($_POST["name"]) && isset($_POST["quantity"])) {
                                echo "ola";
                                $scooters = $db->query("INSERT INTO products(id, type_id, name, quantity, image) values(null, 1,?,?,?)", $name, $quantity, file_get_contents($_FILES['image']['tmp_name']));
                                header("Location: ../tables_bikes.php");
                            }
                            break;
                        case "scooters":
                            if (isset($_POST["name"]) && isset($_POST["quantity"])) {
                                $name = $_POST["name"];
                                echo "ola";
                                $quantity = $_POST["quantity"];
                                $scooters = $db->query("INSERT INTO products(id, type_id, name, quantity, image) values(null, 2,?,?,?)", $name, $quantity, file_get_contents($_FILES['image']['tmp_name']));
                                header("Location: ../tables_scooters.php");
                            }
                            break;
                        case "cars":
                            if (isset($_POST["name"]) && isset($_POST["quantity"])) {
                                $name = $_POST["name"];
                                echo "ola";
                                $quantity = $_POST["quantity"];
                                $scooters = $db->query("INSERT INTO products(id, type_id, name, quantity, image) values(null, 3,?,?,?)", $name, $quantity, file_get_contents($_FILES['image']['tmp_name']));
                                header("Location: ../tables_cars.php");
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            }

        case "rents":
            if (isset($_POST["product"]) && isset($_POST["account"]) && isset($_POST["date"]) && isset($_POST["quantity"]) && isset($_POST["plan"])) {
                $product = $_POST["product"];
                $account = $_POST["account"];
                $date = $_POST["date"];
                $quantity = $_POST["quantity"];
                $plan = $_POST["plan"];
                $payment = $_POST["payment"];
                $state = $_POST["state"];


                $rents = $db->query("INSERT INTO rents(id, product_id, account_id, date, quantity, plan, payment, state) values(null, ?,?,?,?,?,?,?)", $product, $account, $date, $quantity, $plan, $payment, $state);
                header("Location: ../tables_rents.php");
            }
            break;

        case "plans":
            if (isset($_POST["hours"]) && isset($_POST["price"])) {
                $hours = $_POST["hours"];
                $price = $_POST["price"];

                $plans = $db->query("INSERT INTO plans(id, hours, price) values(null, ?,?)", $hours, $price);
                header("Location: ../tables_plans.php");
            }
            break;

        case "partners":
            if (isset($_POST["nome"])) {
                $name = $_POST["nome"];

                $partners = $db->query("INSERT INTO partners(id, name, image) values(null, ?,?)", $name, file_get_contents($_FILES['imagem']['tmp_name']));
                header("Location: ../tables_partners.php");
            }
            break;
    }
}


include("../../src/database/close.php");
