<?php
include("../src/database/conn.php");
session_start();
if (!(isset($_SESSION["sid"])))
    header("Location: login.php");

if ($_SESSION["role"] != "ADMIN")
    header("Location: ../index.php");
?>
<!DOCTYPE html>
<html lang="pt">

<?php include("layout/header.php") ?>

<body id="page-top">
    <!-- Page Wrapper -->
    <div id="wrapper">
        <!-- Sidebar -->
        <?php include("layout/navbar.php") ?>
        <!-- End of Sidebar -->

        <!-- Content Wrapper -->
        <div id="content-wrapper" class="d-flex flex-column">
            <!-- Main Content -->
            <div id="content">
                <!-- Topbar -->
                <nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">
                    <!-- Sidebar Toggle (Topbar) -->
                    <form class="form-inline">
                        <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
                            <i class="fa fa-bars"></i>
                        </button>
                    </form>

                    <!-- Topbar Navbar -->
                    <ul class="navbar-nav ml-auto">
                        <!-- Nav Item - Search Dropdown (Visible Only XS) -->
                        <li class="nav-item dropdown no-arrow d-sm-none">
                            <a class="nav-link dropdown-toggle" href="#" id="searchDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <i class="fas fa-search fa-fw"></i>
                            </a>
                            <!-- Dropdown - Messages -->
                            <div class="dropdown-menu dropdown-menu-right p-3 shadow animated--grow-in" aria-labelledby="searchDropdown">
                                <form class="form-inline mr-auto w-100 navbar-search">
                                    <div class="input-group">
                                        <input type="text" class="form-control bg-light border-0 small" placeholder="Search for..." aria-label="Search" aria-describedby="basic-addon2" />
                                        <div class="input-group-append">
                                            <button class="btn btn-primary" type="button">
                                                <i class="fas fa-search fa-sm"></i>
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </li>

                        <!-- Nav Item - Alerts -->
                        <li class="nav-item dropdown no-arrow mx-1">
                            <a class="nav-link dropdown-toggle" href="#" id="alertsDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <i class="fas fa-bell fa-fw"></i>
                                <!-- Counter - Alerts -->
                                <span class="badge badge-danger badge-counter">3+</span>
                            </a>
                            <!-- Dropdown - Alerts -->
                            <div class="dropdown-list dropdown-menu dropdown-menu-right shadow animated--grow-in" aria-labelledby="alertsDropdown">
                                <h6 class="dropdown-header">
                                    Alerts Center
                                </h6>

                                <a class="dropdown-item d-flex align-items-center" href="#">
                                    <div class="mr-3">
                                        <div class="icon-circle bg-success">
                                            <i class="fas fa-donate text-white"></i>
                                        </div>
                                    </div>
                                    <div>
                                        <div class="small text-gray-500">
                                            December 7, 2019
                                        </div>
                                        $290.29 has been deposited into your
                                        account!
                                    </div>
                                </a>
                                <a class="dropdown-item d-flex align-items-center" href="#">
                                    <div class="mr-3">
                                        <div class="icon-circle bg-warning">
                                            <i class="fas fa-exclamation-triangle text-white"></i>
                                        </div>
                                    </div>
                                    <div>
                                        <div class="small text-gray-500">
                                            December 2, 2019
                                        </div>
                                        Spending Alert: We've noticed
                                        unusually high spending for your
                                        account.
                                    </div>
                                </a>
                                <a class="dropdown-item text-center small text-gray-500" href="#">Show All Alerts</a>
                            </div>
                        </li>

                        <div class="topbar-divider d-none d-sm-block"></div>

                        <!-- Nav Item - User Information -->
                        <li class="nav-item dropdown no-arrow">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <span class="mr-2 d-none d-lg-inline text-gray-600 small"><?php echo $_SESSION["name"] ?></span>
                                <img class="img-profile rounded-circle" src="img/undraw_profile.svg" />
                            </a>
                            <!-- Dropdown - User Information -->
                            <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in" aria-labelledby="userDropdown">
                                <a class="dropdown-item" href="#">
                                    <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Profile
                                </a>
                                <a class="dropdown-item" href="#">
                                    <i class="fas fa-cogs fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Settings
                                </a>
                                <a class="dropdown-item" href="#">
                                    <i class="fas fa-list fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Activity Log
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item" href="#" data-toggle="modal" data-target="#logoutModal">
                                    <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Logout
                                </a>
                            </div>
                        </li>
                    </ul>
                </nav>
                <!-- End of Topbar -->

                <!-- Begin Page Content -->
                <div class="container-fluid">
                    <!-- Page Heading -->
                    <h1 class="h3 mb-2 text-gray-800">Rents</h1>
                    <p class="mb-4">
                        Here you can see every Rent in your database
                    </p>
                    <div class="container-xl">
                        <div class="table-responsive">
                            <div class="table-wrapper">
                                <div class="table-title">
                                    <div class="row">
                                        <div class="col-sm-6">
                                            <h2>Manage <b>Rents</b></h2>
                                        </div>
                                        <div class="col-sm-6">
                                            <a href="#addEmployeeModal" class="btn btn-success" data-toggle="modal"><i class="material-icons">&#xE147;</i> <span>Add New Rent</span></a>
                                        </div>
                                    </div>
                                </div>
                                <table class="table table-striped table-hover">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Product ID</th>
                                            <th>Account ID</th>
                                            <th>Date</th>
                                            <th>Quantity</th>
                                            <th>Plan</th>
                                            <th>Payment Received</th>
                                            <th>Ready to deliver</th>
                                        </tr>
                                    </thead>
                                    <tbody>

                                        <?php
                                        $rents = $db->query('SELECT * FROM rents')->fetchAll();
                                        foreach ($rents as $rent) {
                                            echo '<tr>
                                            <td>' . $rent["id"] . "</td>
                                            <td>" . $rent["product_id"] . "</td>
                                            <td>" . $rent["account_id"] . "</td>
                                            <td>" . $rent["date"] . "</td>
                                            <td>" . $rent["quantity"] . "</td>
                                            <td>" . $rent["plan"] . "</td>
                                            <td>" . ($rent["payment"] == 0 ? "No" : "Yes") . "</td>
                                            <td>" . ($rent["state"] == 0 ? "No" : "Yes") . '</td>

                                            <td class="text-right">
                                                <a href="#editRentModal" class="edit" data-toggle="modal" data-attribute-id="' . $rent["id"] . '" ><i class="material-icons" data-toggle="tooltip" title="Edit">&#xE254;</i></a>
                                                <a href="#deleteRentModal" class="delete" data-toggle="modal" data-attribute-id="' . $rent["id"] . '" ><i class="material-icons" data-toggle="tooltip" title="Delete">&#xE872;</i></a>
                                            </td>
                                        </tr>';
                                        }

                                        ?>
                                    </tbody>
                                </table>
                                <div class="clearfix">
                                    <div class="hint-text">Showing <b>5</b> out of <b>25</b> entries</div>
                                    <ul class="pagination">
                                        <li class="page-item disabled"><a href="#">Previous</a></li>
                                        <li class="page-item"><a href="#" class="page-link">1</a></li>
                                        <li class="page-item"><a href="#" class="page-link">2</a></li>
                                        <li class="page-item active"><a href="#" class="page-link">3</a></li>
                                        <li class="page-item"><a href="#" class="page-link">4</a></li>
                                        <li class="page-item"><a href="#" class="page-link">5</a></li>
                                        <li class="page-item"><a href="#" class="page-link">Next</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Edit Modal HTML -->
                    <div id="addEmployeeModal" class="modal fade">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form action="db/insert.php" method="POST" enctype="multipart/form-data">
                                    <div class="modal-header">
                                        <h4 class="modal-title">Add Rents</h4>
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="form-group">
                                            <label>Product ID</label>
                                            <input type="text" name="product" class="form-control" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Account ID</label>
                                            <input type="text" name="account" class="form-control" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Date</label>
                                            <input type="datetime-local" name="date" class="form-control" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Quantity</label>
                                            <input type="text" name="quantity" class="form-control" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Plan</label>
                                            <select name="plan" class="plan count form-control">
                                                <option value="1" data-price="6"> 2 Hours - 6€ </option>
                                                <option value="2" data-price="10"> 4 Hours - 10€ </option>
                                                <option value="3" data-price="12"> 6 Hours - 12€ </option>
                                                <option value="4" data-price="14"> 8 Hours - 14€ </option>
                                                <option value="5" data-price="20"> 1 day - 20€ </option>
                                                <option value="6" data-price="30"> 2 days - 30€ </option>
                                                <option value="7" data-price="45"> 3 days - 45€ </option>
                                                <option value="8" data-price="60"> 4 days - 60€ </option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label>Payment received?</label>
                                            <select name="payment" class="form-control" id="">
                                                <option value="0">No</option>
                                                <option value="1">Yes</option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label>Ready to deliver?</label>
                                            <select name="state" class="form-control" id="">
                                                <option value="0">No</option>
                                                <option value="1">Yes</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <input type="button" class="btn btn-default" data-dismiss="modal" value="Cancel">
                                        <input type="submit" class="btn btn-success" value="Add">
                                        <input type="hidden" name="table" value="rents">
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    <!-- Edit Modal HTML -->
                    <div id="editRentModal" class="modal fade">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form action="db/update.php" method="POST">
                                    <div class="modal-header">
                                        <h4 class="modal-title">Edit Rents</h4>
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="form-group">
                                            <label>Product ID</label>
                                            <input type="text" name="product" class="form-control" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Account ID</label>
                                            <input type="text" name="account" class="form-control" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Date</label>
                                            <input type="datetime-local" name="date" class="form-control" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Quantity</label>
                                            <input type="text" name="quantity" class="form-control" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Plan</label>
                                            <select name="plan" class="plan count form-control">
                                                <option value="1" data-price="6"> 2 Hours - 6€ </option>
                                                <option value="2" data-price="10"> 4 Hours - 10€ </option>
                                                <option value="3" data-price="12"> 6 Hours - 12€ </option>
                                                <option value="4" data-price="14"> 8 Hours - 14€ </option>
                                                <option value="5" data-price="20"> 1 day - 20€ </option>
                                                <option value="6" data-price="30"> 2 days - 30€ </option>
                                                <option value="7" data-price="45"> 3 days - 45€ </option>
                                                <option value="8" data-price="60"> 4 days - 60€ </option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label>Payment received?</label>
                                            <select name="payment" class="form-control" id="">
                                                <option value="0">No</option>
                                                <option value="1">Yes</option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label>Ready to deliver?</label>
                                            <select name="state" class="form-control" id="">
                                                <option value="0">No</option>
                                                <option value="1">Yes</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <input type="button" class="btn btn-default" data-dismiss="modal" value="Cancel">
                                        <input type="submit" class="btn btn-success" value="Edit">
                                        <input type="hidden" name="id">
                                        <input type="hidden" name="table" value="rents">
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    <!-- Delete Modal HTML -->
                    <div id="deleteRentModal" class="modal fade">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form action="db/delete.php" method="POST">
                                    <div class="modal-header">
                                        <h4 class="modal-title">Delete Rents</h4>
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                    </div>
                                    <div class="modal-body">
                                        <p>Are you sure you want to delete these Records?</p>
                                        <p class="text-warning"><small>This action cannot be undone.</small></p>
                                    </div>
                                    <div class="modal-footer">
                                        <input type="button" class="btn btn-default" data-dismiss="modal" value="Cancel">
                                        <input type="submit" class="btn btn-danger" value="Delete">
                                        <input type="hidden" name="table" value="rents">
                                        <input type="hidden" name="id">
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- End of Main Content -->
        </div>
        <!-- End of Content Wrapper -->
    </div>
    <!-- End of Page Wrapper -->

    <!-- Scroll to Top Button-->
    <a class="scroll-to-top rounded" href="#page-top">
        <i class="fas fa-angle-up"></i>
    </a>

    <!-- Logout Modal-->
    <div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">
                        Ready to Leave?
                    </h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                </div>
                <div class="modal-body">
                    Select "Logout" below if you are ready to end your
                    current session.
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" type="button" data-dismiss="modal">
                        Cancel
                    </button>
                    <a class="btn btn-primary" href="login.php">Logout</a>
                </div>
            </div>
        </div>
    </div>

    <?php include("layout/footer.php") ?>

</html>
<?php
include("../src/database/close.php"); ?>