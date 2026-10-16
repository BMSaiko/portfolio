<?php

include("../../src/database/conn.php");
if (isset($_POST["table"])) {
    $table = $_POST["table"];
    switch ($table) {
        case "users":
            $id = $_POST["id"];
            $users = $db->query("delete from accounts where id=?", $id);
            header("Location: ../tables_users.php");

            break;
        case "products":
            if (isset($_POST["id"])) {
                $id = $_POST["id"];
                echo $_POST["table"];

                if (isset($_POST["tipo"])) {
                    $tipo = $_POST["tipo"];
                    switch ($tipo) {
                        case "bikes":

                            $bikes = $db->query("delete from products where id=?", $id);
                            header("Location: ../tables_bikes.php");

                        case "scooters":

                            $scooters = $db->query("delete from products where id=?", $id);
                            header("Location: ../tables_scooters.php");

                        case "cars":

                            $cars = $db->query("delete from products where id=?", $id);
                            header("Location: ../tables_cars.php");

                        default:
                            break;
                    }
                }

                break;
            }
        case "rents":
            if (isset($_POST["id"])) {
                $id = $_POST["id"];
                $rents = $db->query("delete from rents where id=?", $id);
                header("Location: ../tables_rents.php");
            }
            break;
        case "plans":
            if (isset($_POST["id"])) {
                $id = $_POST["id"];
                $plans = $db->query("delete from plans where id=?", $id);
                header("Location: ../tables_plans.php");
            }
            break;
        case "partners":
            if (isset($_POST["id"])) {
                $id = $_POST["id"];
                $partners = $db->query("delete from partners where id=?", $id);
                header("Location: ../tables_partners.php");
            }
            break;
    }
}

include("../../src/database/close.php");
