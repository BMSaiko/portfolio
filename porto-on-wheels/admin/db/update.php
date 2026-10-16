<?php

include("../../src/database/conn.php");
// echo $_POST["quantity"];
// echo $_POST["name"] . "<br>";
// echo file_get_contents($_FILES['image']['tmp_name']) . "<br>";

if (isset($_POST["table"])) {
    $table = $_POST["table"];
    switch ($table) {
        case "users":
            if (isset($_POST["name"]) && isset($_POST["phone"]) && isset($_POST["email"]) && isset($_POST["role"]) && isset($_POST["password"]) && isset($_POST["id"])) {
                $name = $_POST["name"];
                $phone = $_POST["phone"];
                $email = $_POST["email"];
                $role = strtoupper($_POST["role"]);
                $password = $_POST["password"];
                $id = $_POST["id"];


                $accounts = $db->query("update accounts set name=?, phone_number=?, email=?, password=?, role=? where id=?", $name, $phone, $email, $password, $role, $id);
                header("Location: ../tables_users.php");
            } else {
                echo "ADEUS";
            }
            break;

        case "products":
            if (isset($_POST["name"]) && isset($_POST["quantity"]) && isset($_POST["id"])) {
                $name = $_POST["name"];
                $quantity = $_POST["quantity"];
                $id = $_POST["id"];

                if (isset($_POST["tipo"])) {
                    $tipo = $_POST["tipo"];
                    switch ($tipo) {
                        case "bikes":
                            if (isset($_POST["name"]) && isset($_POST["quantity"])) {
                                echo "ola bikes";
                                $bikes = $db->query("update products set name=?, quantity=?, image=? where id=?", $name, $quantity, file_get_contents($_FILES['image']['tmp_name']), $id);
                                header("Location: ../tables_bikes.php");
                            }
                            break;
                        case "scooters":
                            if (isset($_POST["name"]) && isset($_POST["quantity"])) {
                                $name = $_POST["name"];
                                echo "ola";
                                $quantity = $_POST["quantity"];
                                $scooters = $db->query("update products set name=?, quantity=?, image=? where id=?", $name, $quantity, file_get_contents($_FILES['image']['tmp_name']), $id);
                                header("Location: ../tables_scooters.php");
                            }
                            break;
                        case "cars":
                            if (isset($_POST["name"]) && isset($_POST["quantity"])) {
                                $name = $_POST["name"];
                                echo "ola cars";
                                $quantity = $_POST["quantity"];
                                $scooters = $db->query("update products set name=?, quantity=?, image=? where id=?", $name, $quantity, file_get_contents($_FILES['image']['tmp_name']), $id);
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
            if (isset($_POST["product"]) && isset($_POST["account"]) && isset($_POST["date"]) && isset($_POST["quantity"]) && isset($_POST["plan"]) && isset($_POST["id"])) {
                $product = $_POST["product"];
                $account = $_POST["account"];
                $date = $_POST["date"];
                $quantity = $_POST["quantity"];
                $plan = $_POST["plan"];
                $id = $_POST["id"];
                $payment = $_POST["payment"];
                $state = $_POST["state"];
                echo $payment . "<br>";
                echo $state . "<br>";

                $rents = $db->query("update rents set product_id=?, account_id=?, date=?, quantity=?, plan=?, payment=?, state=? where id=?", $product, $account, $date, $quantity, $plan, $payment, $state, $id);

                header("Location: ../tables_rents.php");
            } else {
                echo "ADEUS";
            }
            break;

        case "plans":
            if (isset($_POST["hours"]) && isset($_POST["price"]) && isset($_POST["id"])) {
                $hours = $_POST["hours"];
                $price = $_POST["price"];
                $id = $_POST["id"];

                $plans = $db->query("update plans set hours=?, price=? where id=?", $hours, $price, $id);
                header("Location: ../tables_plans.php");
            }
            break;

        case "partners":
            if (isset($_POST["nome"]) && isset($_POST["id"])) {
                $name = $_POST["nome"];
                $id = $_POST["id"];

                $partners = $db->query("update partners set name=?, image=? where id=?", $name, file_get_contents($_FILES['imagem']['tmp_name']), $id);
                header("Location: ../tables_partners.php");
            } else {
                echo "ADEUS";
            }
            break;
    }
}


include("../../src/database/close.php");
