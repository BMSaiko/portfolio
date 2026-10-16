<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Scooters | Porto On Wheels</title>
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
                                <button class="tiny" title="Work in progress" onclick="window.location.href = 'cart.php'">Checkout</button>
                            </div>
                        </div>
                        <div id="cartItems">Loading cart...</div>
                        Total price: <strong id="totalPrice">0 €</strong>
                    </div>

                </div>

                <div class="medium-8 columns">


                    <h2 style="margin-bottom: 30px;">Our Scooters</h2>
                    <?php include("src/database/conn.php");
                    $product = $db->query('SELECT * FROM products where type_id = 2')->fetchAll();
                    foreach ($product as $prod) {
                    ?>
                        <div class="product medium-4 columns" data-name="<?= $prod["name"] ?>" data-price="0" data-id="<?= $prod["id"] ?>">
                            <?= '<img class="cartIMG" src="data:image/png;base64,' . base64_encode($prod['image']) . '"/>'; ?>
                            <h4 class="cart-text"><?php echo $prod["name"] ?></h4>
                            <p>Discover the world with comfort and convenience, renting the best properties for your travels with us.</p>
                            <input type="text" class="count" value="1" />
                            <select name="plan" class="plan count">
                                <option value="1" data-price="6"> 2 Hours - 6€ </option>
                                <option value="2" data-price="10"> 4 Hours - 10€ </option>
                                <option value="3" data-price="12"> 6 Hours - 12€ </option>
                                <option value="4" data-price="14"> 8 Hours - 14€ </option>
                                <option value="5" data-price="20"> 1 day - 20€ </option>
                                <option value="6" data-price="30"> 2 days - 30€ </option>
                                <option value="7" data-price="45"> 3 days - 45€ </option>
                                <option value="8" data-price="60"> 4 days - 60€ </option>
                            </select>
                            <button type="button" class="btn cartButton"> Add Cart</button>
                        </div>
                    <?php
                    }
                    ?>
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