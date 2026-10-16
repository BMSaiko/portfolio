<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Register | Porto On Wheels</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-aFq/bzH65dt+w6FI2ooMVUpc+21e0SRygnTpmBvdBgSdnuTN7QbdgL+OapgHtvPp" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
    <link rel="stylesheet" href="assets/css/style.css" />
    <link rel="stylesheet" href="assets/css/header.css" />
    <link rel="stylesheet" href="assets/css/footer.css" />
    <link rel="stylesheet" href="assets/css/login.css" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/js/bootstrap.bundle.min.js" integrity="sha384-qKXV1j0HvMUeCBQ+QVp7JcfGl760yU08IQ+GpUo5hlbpg51QRiuqHAJz8+BrxE/N" crossorigin="anonymous"></script>
    <script src="assets/js/main.js"></script>
</head>

<body>
    <?php include("src/layout/header.php") ?>
    <!-- TODO: Fazer a verificação se a conta existe -->
    <?php

    if ((isset($_SESSION["sid"])))
        header("Location: index.php");


    ?>
    <section>
        <div class="h-100 gradient-form">
            <div class="container py-5 h-100">
                <div class="row d-flex justify-content-center align-items-center h-100">
                    <div class="col-xl-10">
                        <div class="card rounded-3 text-black">
                            <div class="row g-0">
                                <div class="col-lg-6">
                                    <div class="card-body p-md-5 mx-md-4">
                                        <div class="text-center" style="padding-bottom: 20px">
                                            <img src="./assets/img/logo.png" alt="Porto on Two Wheels Logo" id="logo-header" />
                                        </div>
                                        <form action="src/database/auth.php" method="POST">

                                            <div class="form-outline mb-4">
                                                <label class="form-label" for="form2Example11">Name</label>
                                                <input type="text" name="name" class="form-control" placeholder="Your name" />
                                            </div>

                                            <div class="form-outline mb-4">
                                                <label class="form-label" for="form2Example11">Phone Number</label>
                                                <input type="tel" name="phone_number" class="form-control" placeholder="Your phone number" />
                                            </div>
                                            <div class="form-outline mb-4">
                                                <label class="form-label" for="form2Example11">Email</label>
                                                <input type="email" name="email" class="form-control" placeholder="Your email address" />
                                            </div>

                                            <div class="form-outline mb-4">
                                                <label class="form-label" for="form2Example22">Password</label>
                                                <input type="password" name="password" class="form-control" placeholder="*******" />
                                            </div>

                                            <div class="text-center pt-1 mb-5 pb-1">
                                                <button class="Button-Login" type="submit" name="Button-Login">
                                                    Register
                                                </button>
                                                <a style="
                                                            margin-left: 20px;
                                                        " class="text-muted" href="login.php">Already have an account?</a>
                                            </div>



                                        </form>
                                    </div>
                                </div>
                                <div class="col-lg-6 d-flex align-items-center gradient-custom-2">
                                    <div class="text-white px-3 py-4 p-md-5 mx-md-4">
                                        <h4 class="mb-4">
                                            We are more than just a company
                                        </h4>
                                        <p class="small mb-0">
                                            We're a renting company that
                                            cares about sustainability and
                                            nature preservation. We offer
                                            bike, scooter, and low-carbon
                                            car rental services to reduce
                                            the environmental impact of
                                            individual transportation. Join
                                            us in our mission to promote a
                                            more sustainable and responsible
                                            lifestyle.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <?php include("src/layout/footer.php") ?>
</body>

</html>