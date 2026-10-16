<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Contacts | Porto On Wheels</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-aFq/bzH65dt+w6FI2ooMVUpc+21e0SRygnTpmBvdBgSdnuTN7QbdgL+OapgHtvPp" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
    <link rel="stylesheet" href="assets/css/style.css" />
    <link rel="stylesheet" href="assets/css/contact.css" />
    <link rel="stylesheet" href="assets/css/aboutus.css" />
    <link rel="stylesheet" href="assets/css/header.css" />
    <link rel="stylesheet" href="assets/css/footer.css" />
    <link rel="stylesheet" href="assets/css/styleBanner.css" />
    <link rel="stylesheet" href="assets/css/styleTipoVeiculos.css" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha2/dist/js/bootstrap.bundle.min.js" integrity="sha384-qKXV1j0HvMUeCBQ+QVp7JcfGl760yU08IQ+GpUo5hlbpg51QRiuqHAJz8+BrxE/N" crossorigin="anonymous"></script>
    <script src="assets/js/main.js"></script>
    <script src="assets/js/contact.js"></script>

    <link href="https://api.mapbox.com/mapbox-gl-js/v2.13.0/mapbox-gl.css" rel="stylesheet" />
</head>

<body>
    <?php include("src/layout/header.php") ?>

    <section class="contactos-container">
        <h1 class="TitleContactosBanner">
            RENT WITH PORTO ON WHEELS
        </h1>

        <div class="container">
            <div class="row d-flex justify-content-center align-items-center">
                <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 contactBorder me-lg-5 me-xl-5 mb-5">
                    <a href="mailto:geral@portoonwheels.com" target="_blank">
                        <div class="productHeader text-center px-4 pt-4">
                            <img class="productImg" src="./assets/img/email.png" alt="Email" />
                        </div>
                        <div class="text-center">
                            <h4 id="contact-text"class="our-values-title pb-4">geral@portoonwheels.com</h4>
                        </div>

                    </a>
                </div>
                <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 contactBorder me-xl-5 mb-5">
                    <a href="tel:+351220986439" target="_blank">

                        <div class="productHeader text-center px-4 pt-4">
                            <img class="productImg" src="./assets/img/telephone.png" alt="Telephone" />
                        </div>
                        <div class="text-center">
                            <h4 id="contact-text"class="our-values-title pb-4">+351 220 986 439</h4>
                        </div>
                    </a>
                </div>

                <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 contactBorder me-lg-5 me-xl-5 mb-5">
                    <a href="tel:+351913074570" target="_blank">
                        <div class="productHeader text-center px-4 mt-2 pt-4">
                            <img class="productImg" src="./assets/img/phone.png" alt="Telephone" />
                        </div>
                        <div class="text-center">
                            <h4 id="contact-text" class="our-values-title pb-4">+351 913 074 570</h4>
                        </div>


                    </a>
                </div>
            </div>
        </div>
    </section>
    <section id="mapa">
        <div class="container text-center mt-5">
            <h1 class="ourProductHeader">Where we are:</h1>
        </div>

        <div class="container text-center">
            <div class="row">
                <div class="col-1"></div>
                <div class="col-10">
                    <div id="map" style="width: 100%; height: 300px"></div>
                </div>
                <div class="col-1"></div>
            </div>
        </div>
        <div class="container-fluid mt-5 mb-5">
            <div class="row">
                <div class="col"></div>
                <div class="col"></div>
                <div class="col"></div>
            </div>
        </div>
        <script src="https://api.mapbox.com/mapbox-gl-js/v2.13.0/mapbox-gl.js"></script>
        <script>
            mapboxgl.accessToken =
                "<MAPBOX_PUBLIC_TOKEN>";
            const map = new mapboxgl.Map({
                container: "map", // container ID
                style: "mapbox://styles/mapbox/streets-v12", // style URL
                center: [-8.6291, 41.1579], // starting position [lng, lat]°
                zoom: 14, // starting zoom
            });
            console.log(map);
            const coords = {
                type: "FeatureCollection",
                features: [{
                        type: "Feature",
                        properties: {
                            description: "Descrição",
                        },
                        geometry: {
                            type: "Point",
                            coordinates: [
                                -8.638463221163832, 41.15940066957223,
                            ],
                        },
                    },
                    {
                        type: "Feature",
                        properties: {
                            description: "Aliqua reprehenderit ut consectetur amet ipsum non irure occaecat. Quis incididunt ea ipsum ex. Nisi amet veniam amet exercitation duis voluptate dolor minim in do Lorem laboris excepteur.",
                        },
                        geometry: {
                            type: "Point",
                            coordinates: [
                                -8.627754493471487, 41.15800386701292,
                            ],
                        },
                    },
                    {
                        type: "Feature",
                        properties: {
                            description: "Descrição",
                        },
                        geometry: {
                            type: "Point",
                            coordinates: [
                                -8.630438051851812, 41.15478015291127,
                            ],
                        },
                    },
                ],
            };

            // add markers to map
            for (const feature of coords.features) {
                // create a HTML element for each feature
                const el = document.createElement("div");
                el.className = "marker";

                // make a marker for each feature and add to the map
                new mapboxgl.Marker(el)
                    .setLngLat(feature.geometry.coordinates)
                    .setPopup(
                        new mapboxgl.Popup() // add popups
                        .setHTML(
                            `<p class="mt-3">${feature.properties.description}</p>`,
                        ),
                    )
                    .addTo(map);
            }
        </script>
    </section>

    <?php include("src/layout/footer.php") ?>

</body>

</html>