<?php session_start(); ?>
<link rel="stylesheet" href="assets/css/header.css" />
<header>
    <nav class="navbar navbar-expand-lg navbar-dark ftco_navbar ftco-navbar-light site-navbar-target" id="ftco-navbar">
        <div class="container">
            <a class="navbar-brand" href="index.php">
                <img src=" ./assets/img/logo.png" id="logo-header">
            </a>
            <button class="navbar-toggler js-fh5co-nav-toggle fh5co-nav-toggle" type="button" data-toggle="collapse">
                <span style="color:black" class="oi oi-menu"></span> Menu
            </button>
            <div class="collapse navbar-collapse" id="ftco-nav">
                <ul class="navbar-nav nav ml-auto d-flex justify-content-center align-items-center">
                    <li class="nav-item"><a href="index.php" class="nav-link"><span>Home</span></a></li>
                    <li class="nav-item"><a href="index.php#renting" class="nav-link"><span>Renting</span></a></li>
                    <li class="nav-item"><a href="about-us.php" class="nav-link"><span>About Us</span></a></li>
                    <li class="nav-item"><a href="contact.php" class="nav-link"><span>Contact</span></a></li>
                    <?php
                    if (isset($_SESSION["role"]) && isset($_SESSION["sid"])) {

                        if (isset($_SESSION["profile_image"])) {
                            echo '<a class="nav-item nav-link d-flex justify-content-center align-items-center" href="user-profile.php"><img class="rounded-circle p-1" width="60" src="data:image/png;base64,' . base64_encode($_SESSION["profile_image"]) . '"/></a>';
                        } else {
                            echo '<a class="nav-item nav-link" href="user-profile.php"><img src="https://bootdey.com/img/Content/avatar/avatar6.png" alt="User" class="rounded-circle p-1" width="60"></a>';
                        }
                    } else {
                        echo '<a class="nav-item nav-link" href="login.php">
                            <i class="fa fa-user-circle" id="login-button"></i>
                        </a>';
                    }

                    ?>
                </ul>
            </div>
        </div>
    </nav>

</header>