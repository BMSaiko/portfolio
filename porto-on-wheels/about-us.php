<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>About Us | Porto On Wheels</title>
    <link rel="stylesheet" href="assets/css/style.css" />
    <link rel="stylesheet" href="assets/css/contact.css" />
    <link rel="stylesheet" href="assets/css/aboutus.css" />
    <link rel="stylesheet" href="assets/css/footer.css" />
    <link rel="stylesheet" href="assets/css/styleBanner.css" />
    <link rel="stylesheet" href="assets/css/styleTipoVeiculos.css" />
    <link href="https://api.mapbox.com/mapbox-gl-js/v2.13.0/mapbox-gl.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-aFq/bzH65dt+w6FI2ooMVUpc+21e0SRygnTpmBvdBgSdnuTN7QbdgL+OapgHtvPp" crossorigin="anonymous" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/js/bootstrap.bundle.min.js" integrity="sha384-qKXV1j0HvMUeCBQ+QVp7JcfGl760yU08IQ+GpUo5hlbpg51QRiuqHAJz8+BrxE/N" crossorigin="anonymous"></script>
    <script src="assets/js/main.js"></script>
    <script src="assets/js/about-us.js"></script>
</head>

<body>
    <?php include("src/layout/header.php") ?>
    <section class="our-mission">
    </section>
    <section class="our-values">
        <div class="container text-center mt-5">
            <h1 class="values">Our Values</h1>
            <div class="container">
                <div class="row d-flex justify-content-center align-items-center">
                    <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 valuesBorder me-lg-5 me-xl-5 mb-5">
                        <div class="text-center pt-4">

                            <h2 class="our-values-title">Nature Preservation</h2>
                            <img class="valuesImg" src="./assets/img/lake.gif" alt="Nature Preservation" />

                        </div>

                    </div>
                    <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 valuesBorder me-xl-5 mb-5">
                        <div class="text-center pt-4">

                            <h2 class="our-values-title">Sustainability</h2>
                            <img class="valuesImg" src="./assets/img/eco-earth.gif" alt="Sustainability" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="text-values">
        <div class="text-center bannerDiv" style="background-color:#63A0A6">
            <div class="">
                <h5 class="textMainBanner ps-5 pe-5" id="mission">
                    We value sustainability and nature preservation in everything we do. We adopt eco-friendly practices in our production chain and raise awareness among our employees and partners about the importance of preserving the environment for a greener and more balanced future.
                </h5>

            </div>
        </div>
    </section>
    <section class="our-clients">
        <div class="carousel slide" id="about-us-padding-slider" data-bs-ride="carousel">
            <!-- Slides -->
            <div class="carousel-inner">
                <div class="carousel-item active">
                    <div class="row">
                        <div class="col-sm-3">
                            <img src="./assets/img/tt-3ways (1).jpg" class="d-block w-100 carroussel-image" >
                        </div>
                        <div class="col-sm-3">
                            <img src="./assets/img/tt3ways.jpg" class="d-block w-100 carroussel-image" >
                        </div>
                        <div class="col-sm-3">
                            <img src="./assets/img/rent-a-bike-in-porto.jpg" class="d-block w-100 carroussel-image" >
                        </div>
                        <div class="col-sm-3">
                            <img src="./assets/img/20200114-125659-largejpg.jpg" class="d-block w-100 carroussel-image" >
                        </div>
                    </div>
                </div>
                <div class="carousel-item">
                    <div class="row">
                        <div class="col-sm-3">
                            <img src="./assets/img/img-20200825-wa0014-largejpg.jpg" class="d-block w-100 carroussel-image">
                        </div>
                        <div class="col-sm-3">
                            <img src="./assets/img/a-bicicleta-que-andei.jpg" class="d-block w-100 carroussel-image" >
                        </div>
                        <div class="col-sm-3">
                            <img src="./assets/img/a-melhor-trip-pelo-porto.jpg" class="d-block w-100 carroussel-image">
                        </div>
                        <div class="col-sm-3">
                            <img src="./assets/img/tt-3ways.jpg" class="d-block w-100 carroussel-image">
                        </div>
                    </div>
                </div>
            </div>

            <!-- Controls -->
            <a class="carousel-control-prev" href="#about-us-padding-slider" role="button" data-bs-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Previous</span>
            </a>
            <a class="carousel-control-next" href="#about-us-padding-slider" role="button" data-bs-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Next</span>
            </a>
        </div>

    </section>
    <?php include("src/layout/footer.php") ?>
</body>

</html>