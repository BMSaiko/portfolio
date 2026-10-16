<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Rent Form | Porto On Wheels</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-aFq/bzH65dt+w6FI2ooMVUpc+21e0SRygnTpmBvdBgSdnuTN7QbdgL+OapgHtvPp" crossorigin="anonymous" />

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
    <link href="https://unpkg.com/gijgo@1.9.14/css/gijgo.min.css" rel="stylesheet" type="text/css" />

    <link rel="stylesheet" href="assets/css/header.css" />
    <link rel="stylesheet" href="assets/css/footer.css" />
    <link rel="stylesheet" href="assets/css/style.css" />
</head>

<body>
    <?php include("src/layout/header.php") ?>
    <?php

    if (!(isset($_SESSION["sid"])))
        header("Location: login.php");

    ?>
    <!-- <p>
            TODO: este formulario tem de ser geral, ou seja podemos adicionar
            aqui o tipo tambem mas depois temos de ver como vamos fazer para o
            backend ou simplesmente manda-se os planos pelo url e depois pelo
            php sacamos isso
        </p>
        <p>TODO: meter isto a mandar emails</p>
        <p>TODO: tirar campos a mais</p>
        <p>TODO: Fazer um botão para completar o formulario</p> -->
    <div class="text-center d-flex justify-content-center align-items-center" style="width: 100%; height: 150px; background-color: #053d42">
        <h1 class="text-white" style="font-size: 50px">
            RENT LOREM IPSUM
        </h1>
    </div>

    <div class="container text-center mb-5">
        <div class="row">
            <div class="col-sm-12 col-md-12 col-lg-3 col-xl-3"></div>
            <div class="col-sm-12 col-md-12 col-lg-6 col-xl-6 mt-5 text-start">
                <div class="card border-opacity-10">
                    <div class="card-header text-white" style="background-color: #053d42">
                        <h4>Your renting details:</h4>
                    </div>
                    <div class="card-body">
                        <form action="src/database/saveRent.php" method="POST">
                            <div class="mb-3">
                                <label for="nameFormControl" class="form-label">Name</label>
                                <input type="text" class="form-control" id="nameFormControl" placeholder="Your name" required />
                            </div>
                            <div class="row mb-3">
                                <div class="col-sm-12 col-md-12 col-lg-6 col-xl-6">
                                    <label for="emailFormControl" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="emailFormControl" placeholder="example@portoonwheels.com" required />
                                </div>
                                <div class="col-sm-12 col-md-12 col-lg-6 col-xl-6">
                                    <label for="phoneFormControl" class="form-label">Phone</label>
                                    <input type="text" class="form-control" id="phoneFormControl" placeholder="Your phone number" required />
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-12 col-md-12 col-lg-12 col-xl-12">
                                    <label for="datepicker" class="form-label">Rent date</label>
                                    <input type="text" name="rentDate" id="datepicker" placeholder="yyyy-dd-mm HH:MM" required />
                                </div>
                            </div>
                            <div class="w-100 text-center text-white mb-4 mt-4 pb-1 pt-2" style="background-color: #6dabb0">
                                <h5>
                                    Total to pay: <strong id="totalPrice"></strong> (100€ deposit insurance)
                                </h5>
                            </div>
                            <div>
                                <input type="hidden" id="quantity" value="0">
                                <input type="hidden" name="userID" value="<?= $_SESSION["id"] ?>">
                                <input type="hidden" name="products" id="products">
                                <button type="submit" class="w-100 btn text-white fs-4" onclick="sendEmail()" style="background-color: #053d42">
                                    Submit
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <div class="col-3"></div>
        </div>
    </div>

    <?php include("src/layout/footer.php") ?>

    <?php
    if (isset($_SESSION["sid"])) {
        echo "<script>" .
            "document.getElementById('nameFormControl').value = '" . $_SESSION["name"] . "'" . PHP_EOL .
            "document.getElementById('emailFormControl').value = '" . $_SESSION["email"] . "'" . PHP_EOL .
            "document.getElementById('phoneFormControl').value = '" . $_SESSION["phone_number"] . "'" . PHP_EOL .
            "</script>";
    }
    ?>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/@emailjs/browser@3/dist/email.min.js">
    </script>
    <script type="text/javascript">
        (function() {
            emailjs.init("kiEIwPVtM1xPJSWoG");
        })();

        function sendEmail() {

            var templateParams = {
                to_name: document.getElementById('nameFormControl').value,
                to_email: document.getElementById('emailFormControl').value,
                rent_date: document.getElementById('datepicker').value,
                quantity: document.getElementById('quantity').value,
                price: parseInt(document.getElementById("totalPrice").innerText)
            };

            emailjs.send('service_2t8s1ni', 'template_d3xcan2', templateParams)
                .then(function(response) {
                    console.log('SUCCESS!', response.status, response.text);
                    window.location = "index.php"
                }, function(error) {
                    console.log('FAILED...', error);
                });
        }
    </script>
    <script src="https://code.jquery.com/jquery-3.6.4.min.js" integrity="sha256-oP6HI9z1XaZNBrJURtCoUT5SUnxFr8s3BzRl+cbzUq8=" crossorigin="anonymous"></script>
    <script src="https://unpkg.com/gijgo@1.9.14/js/gijgo.min.js" type="text/javascript"></script>
    <script src="https://unpkg.com/@popperjs/core@2" type="text/javascript"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/js/bootstrap.bundle.min.js" integrity="sha384-qKXV1j0HvMUeCBQ+QVp7JcfGl760yU08IQ+GpUo5hlbpg51QRiuqHAJz8+BrxE/N" crossorigin="anonymous"></script>
    <script>
        window.onload = () => {
            Array.prototype.forEach.call(
                document.getElementsByClassName("gj-icon"),
                function(el) {
                    // Do stuff here
                    el.classList.add("d-flex");
                },
            );
        };

        $("#datepicker").datetimepicker({
            footer: true,
            uiLibrary: "bootstrap5",
            format: "yyyy-dd-mm HH:MM",
            datepicker: {
                disableDates: function(date) {
                    // allow for today
                    const currentDate = new Date().setHours(0, 0, 0, 0);
                    return date.setHours(0, 0, 0, 0) >= currentDate ?
                        true :
                        false;
                },
            },
        });
    </script>
    <script src="assets/js/cartForms.js"></script>

    <script src="assets/js/main.js"></script>
</body>

</html>