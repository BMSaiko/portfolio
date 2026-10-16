<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Porto On Wheels</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-aFq/bzH65dt+w6FI2ooMVUpc+21e0SRygnTpmBvdBgSdnuTN7QbdgL+OapgHtvPp" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
    <link rel="stylesheet" href="assets/css/style.css" />
    <link rel="stylesheet" href="assets/css/header.css" />
    <link rel="stylesheet" href="assets/css/footer.css" />
    <link rel="stylesheet" href="assets/css/partners.css" />
    <link rel="stylesheet" href="assets/css/styleBanner.css" />
    <link rel="stylesheet" href="assets/css/styleTipoVeiculos.css" />
    <link rel="stylesheet" href="assets/sass/style.scss" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/js/bootstrap.bundle.min.js" integrity="sha384-qKXV1j0HvMUeCBQ+QVp7JcfGl760yU08IQ+GpUo5hlbpg51QRiuqHAJz8+BrxE/N" crossorigin="anonymous"></script>


    <link rel="stylesheet" type="text/css" href="assets/css/owl.carousel.min.css">
    <link rel="stylesheet" type="text/css" href="assets/css/reviews.css">

</head>

<body>
    <?php include("src/layout/header.php") ?>
    <section id="Banner Inicio">
        <div class="text-center bannerDiv">
            <div class="">
                <h1 class="textMainBanner">
                    RENT A BIKE WITH<br />PORTO ON WHEELS
                </h1>

                <button type="button" onclick="location.href='#renting'" class="btn bannerButton">
                    <a href="#renting">See our Product</a>
                </button>
            </div>
        </div>
    </section>
    <section id="Renting">
        <div class="container text-center mt-5">
            <h1 class="ourProductHeader" id="renting">Our Products</h1>
            <div class="container">
                <div class="row d-flex justify-content-center align-items-center">
                    <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 productBorder me-lg-5 me-xl-5 mb-5">
                        <div class="text-center pt-4">
                            <h1 class="">Bikes</h1>
                            <img class="productImg" src="./assets/img/bicicleta20final201-removebg-preview.png" alt="scooters" />
                        </div>
                        <div>
                            <button type="button" class="btn productButton mb-4" onclick="window.location.href='bikes.php'">
                                Rent Now
                            </button>
                        </div>
                        <div class="productHeader text-start px-4 pb-5">
                            <p>
                                Explore the city on two wheels! Rent a bike now and enjoy a unique adventure. With our wide variety of bikes, you'll surely find one that suits your needs. <br>Book now and experience the freedom of cycling around the city!
                            </p>
                        </div>
                    </div>
                    <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 productBorder me-xl-5 mb-5">
                        <div class="text-center pt-4">
                            <h1 class="">Scooters</h1>
                            <img class="productImg" src="./assets/img/scooter_final_1-transformed.png" alt="scooters" />
                        </div>
                        <div>
                            <button type="button" class="btn productButton mb-4" onclick="window.location.href='scooters.php'">
                                Rent Now
                            </button>
                        </div>
                        <div class="productHeader text-start px-4 pb-5">
                            <p>
                                Speed through the city streets in style! Rent a scooter now and feel the thrill of driving an agile and fun vehicle. With a scooter, you can get anywhere in minutes. <br>Book now and experience the excitement of riding a scooter! </p>
                        </div>
                    </div>
                    <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 productBorder me-lg-5 me-xl-5 mb-5">
                        <div class="text-center pt-4">
                            <h1 class="">Cars</h1>
                            <img class="productImg" src="./assets/img/carro-transformed.png" alt="scooters" />
                        </div>
                        <div>
                            <button type="button" class="btn productButton mb-4 " onclick="window.location.href='cars.php'">
                                Rent Now
                            </button>
                        </div>
                        <div class="productHeader text-start px-4 pb-5">
                            <p>
                                Go anywhere, anytime! Rent a car now and enjoy the freedom to travel at your own pace. With our great selection of cars, you'll find the perfect vehicle for your adventure. <br>Book now and start exploring without limits!</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="Our Partners">
        <div class="container text-center mt-5">
            <h1 class="ourPartners">Our Partners</h1>
            <p class="text-center font-weight-bold" style="color: #63A0A6" id="textPartners">
                We adopt eco-friendly practices in our production chain and raise awareness among our employees and partners </p>

            <div class="slider">
                <div class="carousel slide" id="about-us-padding-slider" data-bs-ride="carousel">
                    <!-- Slides -->
                    <div class="carousel-inner">

                        <div class="carousel-item">
                            <div class="row text-center">

                                <?php include("src/database/conn.php");
                                $partners = $db->query('SELECT * FROM partners LIMIT 4 ')->fetchAll();
                                foreach ($partners as $part) {
                                ?>
                                    <div class="col-sm-3 text-center d-flex justify-content-center align-content-center align-items-center">
                                        <?= '<img class="d-block w-50 h-75 carroussel-image" src="data:image/png;base64,' . base64_encode($part['image']) . '"/>'; ?>
                                    </div>
                                <?php
                                }
                                ?>


                            </div>
                        </div>
                        <div class="carousel-item active">
                            <div class="row">

                                <?php
                                $partners = $db->query('SELECT * FROM partners LIMIT 4 offset 4')->fetchAll();
                                foreach ($partners as $part) {
                                ?>
                                    <div class="col-sm-3 text-center d-flex justify-content-center align-content-center align-items-center">
                                        <?= '<img class="d-block w-50 h-75 carroussel-image" src="data:image/png;base64,' . base64_encode($part['image']) . '"/>'; ?>
                                    </div>
                                <?php
                                }
                                ?>
                                <?php include("src/database/close.php") ?>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
    </section>
    <section id="Mission">
        <div class="text-center bannerDiv" style="background-color:#63A0A6">
            <div class="">
                <h5 class="textMainBanner ps-5 pe-5" id="mission">
                    We value sustainability and nature preservation in everything we do. We adopt eco-friendly practices in our production chain and raise awareness among our employees and partners about the importance of preserving the environment for a greener and more balanced future.
                </h5>
            </div>
        </div>
    </section>
    <section id="Add-Ons">
        <div class="container text-center mt-5">
            <h1 class="ourProductHeader" id="renting">Other Products</h1>
            <div class="container">
                <div class="row d-flex justify-content-center align-items-center">
                    <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 productBorder me-lg-5 me-xl-5 mb-5">
                        <div class="text-center pt-4">
                            <h1 class="">Tourist Guide</h1>
                            <img class="productImg" src="assets/img/touristguide.png" alt="Tourist Guide" />
                        </div>
                        <div>
                            <button type="button" class="btn productButton mb-4" onclick="window.location.href='cart.php'">
                                Add to Cart
                            </button>
                        </div>
                        <div class="productHeader text-start px-4 pb-5">
                            <p>
                                Explore the hidden gems and iconic landmarks of our city with a knowledgeable and friendly tour guide by your side. Discover the rich history and culture of our destination, and create unforgettable memories as you immerse yourself in the local sights and sounds.</p>
                        </div>
                    </div>
                    <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 productBorder me-xl-5 mb-5">
                        <div class="text-center pt-4">
                            <h1 class="">Photographer</h1>
                            <img class="productImg" src="assets/img/photographer.png" alt="Photographer" />
                        </div>
                        <div>
                            <button type="button" class="btn productButton mb-4" onclick="window.location.href='cart.php'">
                                Add to Cart
                            </button>
                        </div>
                        <div class="productHeader text-start px-4 pb-5">
                            <p>
                                Immortalize the beauty of the moment and treasure it forever with our professional photography service. From candid shots to stunning portraits, our skilled and experienced photographers will ensure every precious memory is preserved in timeless images </p>
                        </div>
                    </div>
                    <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 productBorder me-lg-5 me-xl-5 mb-5">
                        <div class="text-center pt-4">
                            <h1 class="">Picnic</h1>
                            <img class="productImg" src="assets/img/picnic.png" alt="Picnic" />
                        </div>
                        <div>
                            <button type="button" class="btn productButton mb-4" onclick="window.location.href='cart.php'">
                                Add to Cart
                            </button>
                        </div>
                        <div class="productHeader text-start px-4 pb-5">
                            <p>
                                Escape to the great outdoors and indulge in a sensory feast with our exquisite picnic catering. From the lush greenery to the mouth-watering cuisine, our picnic experience promises to awaken your senses and leave you with unforgettable memories. </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section id="Testimonial" class="section-100">
        <div class="container">
            <p class="text-center font-weight-bold" style="color: #63A0A6">What out customers say about us</p>
            <h1 class="text-center"><b>Testimonials</b></h1>
            <div class="testimonial-view">
                <div class="owl-carousel">
                    <div class="testimonial-box">
                        <!-- <i class="fa fa-quote-left quote-icon"></i> -->
                        <div class="d-flex justify-content-center align-items-center mb-4">
                            <img src="assets/img/user.jpg" alt="" class="user-img mr-2">
                            <div>
                                <h5 class="mb-0"><b>Michael</b></h5>
                                <p class="text-muted mb-0">Graphic Designer</p>
                            </div>
                        </div>
                        <p class="text-center">"Impressive selection and easy checkout process. Highly recommend!"</p>
                    </div>
                    <div class="testimonial-box">
                        <!-- <i class="fa fa-quote-left quote-icon"></i> -->
                        <div class="d-flex justify-content-center align-items-center mb-4">
                            <img src="assets/img/user1.jpg" alt="" class="user-img mr-2">
                            <div>
                                <h5 class="mb-0"><b>David</b></h5>
                                <p class="text-muted mb-0">Software Engineer</p>
                            </div>
                        </div>
                        <p class="text-center">
                            "Great selection and prices, fast shipping. Will keep using this website."</p>
                    </div>
                    <div class="testimonial-box">
                        <!-- <i class="fa fa-quote-left quote-icon"></i> -->
                        <div class="d-flex justify-content-center align-items-center mb-4">
                            <img src="assets/img/user2.jpg" alt="" class="user-img mr-2">
                            <div>
                                <h5 class="mb-0"><b>John</b></h5>
                                <p class="text-muted mb-0">Marketing Manager</p>
                            </div>
                        </div>
                        <p class="text-center">
                            "Excellent quality product, fast shipping. Recommended to friends and family."
                        </p>
                    </div>
                    <div class="testimonial-box">
                        <!-- <i class="fa fa-quote-left quote-icon"></i> -->
                        <div class="d-flex justify-content-center align-items-center mb-4">
                            <img src="assets/img/user3.jpg" alt="" class="user-img mr-2">
                            <div>
                                <h5 class="mb-0"><b>Maria</b></h5>
                                <p class="text-muted mb-0">Sales Representative</p>
                            </div>
                        </div>
                        <p class="text-center">
                            "Lifesaver! Found a specific product I needed and received it quickly."</p>
                    </div>
                    <div class="testimonial-box">
                        <!-- <i class="fa fa-quote-left quote-icon"></i> -->
                        <div class="d-flex justify-content-center align-items-center mb-4">
                            <img src="https://cdn.acidcow.com/pics/20130909/random_cute_girls_49.jpg" alt="" class="user-img mr-2">
                            <div>
                                <h5 class="mb-0"><b>Emily</b></h5>
                                <p class="text-muted mb-0">Event Planner</p>
                            </div>
                        </div>
                        <p class="text-center">"Helpful customer service with quick response time."</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <?php include("src/layout/footer.php") ?>

    <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.3.4/owl.carousel.min.js" integrity="sha512-bPs7Ae6pVvhOSiIcyUClR7/q2OAsRiovw4vAkX+zJbw3ShAeeqezq50RIIcIURq7Oa20rW2n2q+fyXBNcU9lrw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="assets/js/reviews.js"></script>
    <script src="assets/js/partners.js"></script>

    <script src="assets/js/main.js"></script>
</body>

</html>