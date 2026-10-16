<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>User Profile | Porto On Wheels</title>
    <link rel="stylesheet" href="assets/css/style.css" />
    <link rel="stylesheet" href="assets/css/contact.css" />
    <link rel="stylesheet" href="assets/css/aboutus.css" />
    <link rel="stylesheet" href="assets/css/header.css" />
    <link rel="stylesheet" href="assets/css/footer.css" />
    <link rel="stylesheet" href="assets/css/cart.css" />
    <link rel="stylesheet" href="assets/css/user-profile.css" />
    <link rel="stylesheet" href="assets/css/styleBanner.css" />
    <link rel="stylesheet" href="assets/css/styleTipoVeiculos.css" />
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-aFq/bzH65dt+w6FI2ooMVUpc+21e0SRygnTpmBvdBgSdnuTN7QbdgL+OapgHtvPp" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/foundation/5.0.2/css/foundation.css">

</head>

<body>
    <?php include("src/layout/header.php") ?>
    <?php
    include("src/database/conn.php");
    if (!(isset($_SESSION["sid"])))
        header("Location: login.php");
    ?>
    <main>
        <section>
            <div class="container">
                <div class="main-body">
                    <div class="row">
                        <div class="col-lg-4">
                            <div class="card">
                                <div class="card-body">
                                    <div class="d-flex flex-column align-items-center text-center">
                                        <?php
                                        if (isset($_SESSION["profile_image"])) {
                                            echo '<img class="rounded-circle p-1" width="110" src="data:image/png;base64,' . base64_encode($_SESSION["profile_image"]) . '"/>';
                                        } else {
                                            echo '<img src="https://bootdey.com/img/Content/avatar/avatar6.png" alt="User" class="rounded-circle p-1" width="110">';
                                        }
                                        ?>
                                        <div class="mt-3">
                                            <h4><?= $_SESSION['name'] ?></h4>
                                        </div>
                                    </div>

                                    <ul class="list-group list-group-flush">
                                        <li class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                                            <h6 class="mb-0"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-twitter me-2 icon-inline text-info">
                                                    <path d="M23 3a10.9 10.9 0 0 1-3.14 1.53 4.48 4.48 0 0 0-7.86 3v1A10.66 10.66 0 0 1 3 4s-4 9 5 13a11.64 11.64 0 0 1-7 2c9 5 20 0 20-11.5a4.5 4.5 0 0 0-.08-.83A7.72 7.72 0 0 0 23 3z"></path>
                                                </svg>Twitter</h6>
                                            <span class="text-secondary">portoonwheels2023</span>
                                        </li>
                                        <li class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                                            <h6 class="mb-0"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-instagram me-2 icon-inline text-danger">
                                                    <rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect>
                                                    <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
                                                    <line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line>
                                                </svg>Instagram</h6>
                                            <span class="text-secondary">portoonwheels2023</span>
                                        </li>
                                        <li class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                                            <h6 class="mb-0"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-facebook me-2 icon-inline text-primary">
                                                    <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path>
                                                </svg>Facebook</h6>
                                            <span class="text-secondary">portoonwheels2023</span>
                                        </li>
                                    </ul>
                                </div>
                                <button onclick="window.location.href = 'logout.php'" style="background-color: red; font-size: 1.5rem;">Logout</button>
                            </div>
                        </div>
                        <div class="col-lg-8">
                            <div class="card">
                                <div class="card-body">
                                    <form action="src/database/update.php" method="POST" enctype="multipart/form-data">
                                        <div class="row mb-3">
                                            <div class="col-sm-3">
                                                <h6 class="mb-0">Name(*)</h6>
                                            </div>
                                            <div class="col-sm-9 text-secondary">
                                                <input type="text" name="new_name" class="form-control" placeholder="<?= $_SESSION['name'] ?>" required>
                                            </div>
                                        </div>
                                        <div class="row mb-3">
                                            <div class="col-sm-3">
                                                <h6 class="mb-0">Email(*)</h6>
                                            </div>
                                            <div class="col-sm-9 text-secondary">
                                                <input type="email" name="new_email" class="form-control" placeholder="<?= $_SESSION['email'] ?>">
                                            </div>
                                        </div>
                                        <div class="row mb-3">
                                            <div class="col-sm-3">
                                                <h6 class="mb-0">Mobile(*)</h6>
                                            </div>
                                            <div class="col-sm-9 text-secondary">
                                                <input type="text" name="new_phone_number" class=" form-control" placeholder="<?= $_SESSION['phone_number'] ?>">
                                            </div>
                                        </div>
                                        <div class="row mb-3">
                                            <div class="col-sm-3">
                                                <h6 class="mb-0">Profile Picture</h6>
                                            </div>
                                            <div class="col-sm-9 text-secondary">
                                                <input type="file" name="new_image">

                                                <input type="hidden" name="new_id" value=<?= $_SESSION['id'] ?>>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-sm-3"></div>
                                            <div class="col-sm-9 text-secondary">
                                                <input type="submit" class="btn cartButton  mb-4" value="Save Changes">
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                            <?php
                            if (isset($_SESSION["role"])) {
                                if ($_SESSION["role"] == "ADMIN") {
                                    echo '
                                        <div class="card">
                                <div class="card-body pb-2 pt-2 text-center">
                                    <a href="admin/index.php">
                                        <h4>Go to dashboard</h4>
                                    </a>
                                </div>
                            </div>';
                                }
                            }
                            ?>

                        </div>
                        <div>
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="panel">
                                        <div class="panel-body">
                                            <div class="bio-desk">
                                                <h4 class="terques">Bikes Rented </h4>
                                                <?php
                                                $rents = $db->query('SELECT r.date, r.quantity, r.payment, r.state, p.name FROM rents r inner join products p on r.product_id = p.id where account_id=? and p.type_id=1', $_SESSION["id"])->fetchAll();
                                                foreach ($rents as $rent) {
                                                ?>
                                                    <p>
                                                        Bike: <?= $rent["name"] ?> <br>
                                                        > Quantity: <?= $rent["quantity"] ?> <br>
                                                        > Payment: <?= $rent["payment"] == 0 ? "No" : "Yes" ?> <br>
                                                        > State: <?= $rent["state"] == 0 ? "No" : "Yes" ?> <br>
                                                        > Date: <?= $rent["date"] ?> <br>
                                                    </p>
                                                <?php
                                                }
                                                ?>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="panel">
                                        <div class="panel-body">
                                            <div class="bio-desk">
                                                <h4 class="green">Scooters Rented</h4>
                                                <?php
                                                $rents = $db->query('SELECT r.date, r.quantity, r.payment, r.state, p.name FROM rents r inner join products p on r.product_id = p.id where account_id=? and p.type_id=2', $_SESSION["id"])->fetchAll();
                                                foreach ($rents as $rent) {
                                                ?>
                                                    <p>
                                                        Scooter: <?= $rent["name"] ?> <br>
                                                        > Quantity: <?= $rent["quantity"] ?> <br>
                                                        > Payment: <?= $rent["payment"] == 0 ? "No" : "Yes" ?> <br>
                                                        > State: <?= $rent["state"] == 0 ? "No" : "Yes" ?> <br>
                                                        > Date: <?= $rent["date"] ?> <br>
                                                    </p>
                                                <?php
                                                }
                                                ?>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="panel">
                                        <div class="panel-body">
                                            <div class="bio-desk">
                                                <h4 class="purple">Cars Rented</h4>
                                                <?php
                                                $rents = $db->query('SELECT r.date, r.quantity, r.payment, r.state, p.name FROM rents r inner join products p on r.product_id = p.id where account_id=? and p.type_id=3', $_SESSION["id"])->fetchAll();
                                                foreach ($rents as $rent) {
                                                ?>
                                                    <p>
                                                        Car: <?= $rent["name"] ?> <br>
                                                        > Quantity: <?= $rent["quantity"] ?> <br>
                                                        > Payment: <?= $rent["payment"] == 0 ? "No" : "Yes" ?> <br>
                                                        > State: <?= $rent["state"] == 0 ? "No" : "Yes" ?> <br>
                                                        > Date: <?= $rent["date"] ?> <br>
                                                    </p>
                                                <?php
                                                }
                                                ?>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            </div>
            </div>

            </div>
        </section>
    </main>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>

    <?php include("src/layout/footer.php") ?>
</body>

</html>