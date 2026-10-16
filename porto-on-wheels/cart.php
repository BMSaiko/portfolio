    <!DOCTYPE html>
    <html lang="pt">

    <head>
        <meta charset="UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Cart | Porto On Wheels</title>
        <link rel="stylesheet" href="assets/css/style.css" />
        <link rel="stylesheet" href="assets/css/contact.css" />
        <link rel="stylesheet" href="assets/css/aboutus.css" />
        <link rel="stylesheet" href="assets/css/header.css" />
        <link rel="stylesheet" href="assets/css/footer.css" />
        <link rel="stylesheet" href="assets/css/cart.css" />
        <link rel="stylesheet" href="assets/css/styleBanner.css" />
        <link rel="stylesheet" href="assets/css/styleTipoVeiculos.css" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-aFq/bzH65dt+w6FI2ooMVUpc+21e0SRygnTpmBvdBgSdnuTN7QbdgL+OapgHtvPp" crossorigin="anonymous" />
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/foundation/5.0.2/css/foundation.css">
    </head>

    <body>
        <?php include("src/layout/header.php") ?>
        <main>
            <section class="cart">
                <div class="row">
                    <nav class="top-bar" data-topbar>
                        <ul class="title-area">
                            <li class="name">
                                <h1 id="p-white">Your Cart</h1>
                            </li>
                        </ul>
                    </nav>
                    <div class="medium-4  columns">
                        <div class="cart">
                            <h2>Cart items</h2>
                            <div class="row">
                                <div class="medium-6 columns">
                                    <button class="tiny secondary" id="clear">Clear the cart</button>
                                </div>
                                <div class="medium-6 columns">
                                    <button class="tiny" title="Work in progress" onclick="window.location.href = 'formulario.php'">Checkout</button>
                                </div>
                            </div>
                            <div id="cartItems">Loading cart...</div>
                            Total price: <strong id="totalPrice">0 $</strong>
                        </div>

                    </div>

                    <div class="medium-8 columns">

                        <h2 style="margin-bottom: 30px;">Improve your Experience</h2>
                        <div class="row d-flex justify-content-center align-items-center">
                            <div class="product medium-4 columns" data-name="Tourist Guide" data-price="45" data-id="100">
                                <img class="cartIMG" src="assets/img/touristguide.png" alt="tourist guide" />
                                <h4 class="cart-text">Tourist Guide</h4>
                                <p>Discover the world with comfort and convenience, renting the best properties for your travels with us.</p>
                                <input type="hidden" class="count" value="1" />
                                <button type="button" class="btn cartButton"> Add Cart</button>
                            </div>
                            <div class="product medium-4 columns" data-name="Photographer" data-price="125" data-id="101">
                                <img class="cartIMG" src="assets/img/photographer.png" alt="Photographer" />
                                <h4 class="cart-text">Photographer</h4>
                                <p>Capture the moments of your life with the expertise of our professional photographers.</p>
                                <input type="hidden" class="count" value="1" />
                                <button type="button" class="btn cartButton "> Add Cart</button>
                            </div>
                            <div class="product medium-4 columns" data-name="Picnic" data-price="100" data-id="102">
                                <img class="cartIMG" src="assets/img/picnic.png" alt="Piquenique" />
                                <h4 class="cart-text">Picnic</h4>
                                <p>Relax and enjoy the scenery while savoring the delightful treats from our exclusive picnic service.</p>
                                <input type="hidden" class="count" value="1" />
                                <button type="button" class="btn cartButton"> Add Cart</button>
                            </div>
                        </div>

                    </div>

                </div>

                </div>
                <script type="text/template" id="cartT">
                    <% _.each(items, function (item) { %> <div class = "panel"> <h3> <%= item.name %> </h3>  <span class="label">
    <%= item.count %> piece<% if(item.count > 1)
    {%>s
    <%}%> for <%= item.total %>€</span > </div>
    <% }); %>
    </script>


            </section>

        </main>
        <?php include("src/layout/footer.php") ?>
        <script src='https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
        <script src='https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.5.2/underscore-min.js'></script>
        <script src="assets/js/cart.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/js/bootstrap.bundle.min.js" integrity="sha384-qKXV1j0HvMUeCBQ+QVp7JcfGl760yU08IQ+GpUo5hlbpg51QRiuqHAJz8+BrxE/N" crossorigin="anonymous"></script>
        <script src="assets/js/main.js"></script>
    </body>

    </html>