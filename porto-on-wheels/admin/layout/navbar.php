<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">
    <!-- Sidebar - Brand -->
    <a class="sidebar-brand d-flex align-items-center justify-content-center" href="index.php">
        <div class="sidebar-brand-icon"><img src="img/marker_map-removebg-preview.png" width="60" height="50" alt=""></div>
    </a>

    <!-- Divider -->
    <hr class="sidebar-divider my-0" />

    <!-- Nav Item - Dashboard -->
    <li class="nav-item active">
        <a class="nav-link" href="index.php">
            <i class="fas fa-fw fa-tachometer-alt"></i>
            <span>Dashboard</span></a>
    </li>

    <!-- Divider -->
    <hr class="sidebar-divider" />

    <!-- Heading -->
    <div class="sidebar-heading">Tables</div>


    <!-- Nav Item - Tables -->
    <li class="nav-item">
        <a class="nav-link collapsed" data-toggle="collapse" data-target="#collapseProducts" aria-expanded="true" aria-controls="collapseProducts">
            <i class="fas fa-fw fa-table"></i>
            <span>Products</span>
        </a>

        <div id="collapseProducts" class="collapse" aria-labelledby="headingProducts" data-parent="#accordionSidebar">
            <div class="bg-white py-2 collapse-inner rounded">
                <h6 class="collapse-header">Products functionality:</h6>
                <a class="collapse-item" href="tables_bikes.php">Bikes</a>
                <a class="collapse-item" href="tables_scooters.php">Scooters</a>
                <a class="collapse-item" href="tables_cars.php">Cars</a>
            </div>
        </div>
    </li>

    <!-- Nav Item - Tables -->
    <li class="nav-item">
        <a class="nav-link collapsed" href="tables_rents.php">
            <i class="fas fa-fw fa-table"></i>
            <span>Rents</span>
        </a>
    </li>


    <li class="nav-item">
        <a class="nav-link collapsed" href="tables_plans.php">
            <i class="fas fa-fw fa-table"></i>
            <span>Plans</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link collapsed" href="tables_partners.php">
            <i class="fas fa-fw fa-table"></i>
            <span>Partners</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link collapsed" href="tables_users.php">
            <i class="fas fa-fw fa-table"></i>
            <span>Users</span>
        </a>
    </li>

    <!-- Divider -->
    <hr class="sidebar-divider d-none d-md-block" />

    <!-- Sidebar Toggler (Sidebar) -->
    <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
    </div>
</ul>