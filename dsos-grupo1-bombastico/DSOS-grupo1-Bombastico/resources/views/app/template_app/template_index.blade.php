  <!DOCTYPE html>
  <html lang="en">
    <head>
      <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="apple-touch-icon" sizes="76x76" href="{{url('/assets/img/apple-icon.png')}}">
    <link rel="icon" type="image/png" href="{{url('/assets/img/favicon.png')}}">
    <title>
      Portal de PESTI
    </title>
    <!--     Fonts and icons     -->
    <link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,900|Roboto+Slab:400,700" />
    <!-- Nucleo Icons -->
    <link href="{{url('/assets/css/nucleo-icons.css')}}" rel="stylesheet" />
    <link href="{{url('/assets/css/nucleo-svg.css')}}" rel="stylesheet" />
    <!-- Font Awesome Icons -->
    <script src="https://kit.fontawesome.com/42d5adcbca.js" crossorigin="anonymous"></script>
    <!-- Material Icons -->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Round" rel="stylesheet">
    <!-- CSS Files -->
    <link id="pagestyle" href="{{url('assets/css/material-dashboard.css?v=3.1.0')}}" rel="stylesheet" />
    <!-- Nepcha Analytics (nepcha.com) -->
    <!-- Nepcha is a easy-to-use web analytics. No cookies and fully compliant with GDPR, CCPA and PECR. -->
    <script defer data-site="YOUR_DOMAIN_HERE" src="https://api.nepcha.com/js/nepcha-analytics.js"></script>
    </head>
  <body class="g-sidenav-show  bg-gray-200">
    <aside class="sidenav navbar navbar-vertical navbar-expand-xs border-0 border-radius-xl my-3 fixed-start ms-3   bg-gradient-dark" id="sidenav-main">
      <div class="sidenav-header">
        <i class="fas fa-times p-3 cursor-pointer text-white opacity-5 position-absolute end-0 top-0 d-none d-xl-none" aria-hidden="true" id="iconSidenav"></i>
        <a class="navbar-brand m-0" href="{{url('https://isep.ipp.pt/')}}">
          <img src="{{url('assets/img/logo_20230106.png')}}" class="navbar-brand-img img-isep h-100" alt="main_logo">
        </a>
        </div>
  <hr class="horizontal light mt-0 mb-2">
  </div>
  <div class="collapse navbar-collapse  w-auto " id="sidenav-collapse-main">
    <ul class="navbar-nav">
      @if(session('user_type') != 'empresa')
      <li class="nav-item">
        <a class="nav-link text-white @if($page=='Propostas') active bg-gradient-primary @endif" href="{{route('app.main')}}">
          <div class="text-white text-center me-2 d-flex align-items-center justify-content-center" id="propostasIcon">
            <i class="material-icons opacity-10">dashboard</i>
          </div>
          <span class="nav-link-text ms-1">Propostas</span>
        </a>
      </li>
      @endif
      <li class="nav-item">
        <a class="nav-link text-white @if($page=='Projeto')active bg-gradient-primary @endif" href="{{route('app.projeto')}}">
          <div class="text-white text-center me-2 d-flex align-items-center justify-content-center">
            <i class="material-icons opacity-10">library_books</i>
          </div>
          <span class="nav-link-text ms-1">Projetos</span>
        </a>
      </li>
      @if(session('user_type') === 'admin')
      <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle text-white" href="#" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" id="adminLink">
          <div class="text-white text-center me-2 d-flex align-items-center justify-content-center" id="adminIcon">
            <i class="material-icons opacity-10">admin_panel_settings</i>
          </div>
          <span class="nav-link-text ms-1">Administração</span>
        </a>
        <div class="dropdown-menu dropdown-menu-end">
          <a class="dropdown-item" href="{{route('app.pesti')}}">Pesti</a>
          <a class="dropdown-item" href="{{route('app.empresas')}}">Empresas</a>
          <a class="dropdown-item" href="{{route('app.alunos')}}">Alunos</a>
          <!-- Adicione mais opções conforme necessário -->
        </div>
      </li>
      @endif
      <div class="text-center mt-3">
        <a href="{{ route('site.logout') }}" class="btn btn-success btn-sm">
            Sair
        </a>
    </div>    
    </ul>
  </div>
    </aside>
  <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">
      <!-- Navbar -->
      <nav class="navbar navbar-main navbar-expand-lg px-0 mx-4 shadow-none border-radius-xl" id="navbarBlur" data-scroll="true">
        <div class="container-fluid py-1 px-3">
          <nav aria-label="breadcrumb">
            <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0 me-sm-6 me-5">
              <li class="breadcrumb-item text-sm"><a class="opacity-5 text-dark" href="{{route('app.main')}}">{{$app}}</a></li>
              <li class="breadcrumb-item text-sm text-dark active" aria-current="page">{{$page}}</li>
            </ol>
            <h6 class="font-weight-bolder mb-0">{{$page}}</h6>
          </nav>
          <div class="collapse navbar-collapse mt-sm-0 mt-2 me-md-0 me-sm-4" id="navbar">
            <div class="ms-md-auto pe-md-3 d-flex align-items-center">
            </div>
            <ul class="navbar-nav  justify-content-end">
              <li class="nav-item d-xl-none ps-3 d-flex align-items-center">
                <a href="javascript:;" class="nav-link text-body p-0" id="iconNavbarSidenav">
                  <div class="sidenav-toggler-inner">
                    <i class="sidenav-toggler-line"></i>
                    <i class="sidenav-toggler-line"></i>
                    <i class="sidenav-toggler-line"></i>
                  </div>
                </a>
              </li>
              <li class="nav-item px-3 d-flex align-items-center">
                <a href="javascript:;" class="nav-link text-body p-0">
                  <i class="fa fa-cog fixed-plugin-button-nav cursor-pointer"></i>
                </a>
              </li>
              <li class="nav-item d-flex align-items-center">
                <a href="{{route('app.perfil')}}" class="nav-link text-body font-weight-bold px-0">
                  <i class="fa fa-user me-sm-1" @if($page == "perfil")style="color:#ff3399" @endif></i>
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
      </nav>
      <!-- End Navbar -->
      @yield('main_content')
      <footer class="footer py-4  ">
        <div class="container-fluid">
          <div class="row align-items-center justify-content-lg-between">
            <div class="col-lg-6 mb-lg-0 mb-4">
              <div class="copyright text-center text-sm text-muted text-lg-start">
                © <script>
                  document.write(new Date().getFullYear())
                </script>,
                made for Desenvolvimento de Software Orientado a Serviços (DSOS) by
                Bruno,Ricardo,João
              </div>
            </div>
          </div>
        </div>
      </footer>
    </div>
    </main>
    <!--   Core JS Files   -->
    <script src="{{url('assets/js/core/popper.min.js')}}"></script>
    <script src="{{url('assets/js/core/bootstrap.min.js')}}"></script>
    <script src="{{url('assets/js/plugins/perfect-scrollbar.min.js')}}"></script>
    <script src="{{url('assets/js/plugins/smooth-scrollbar.min.js')}}"></script>
    <script src="{{url('assets/js/plugins/chartjs.min.js')}}"></script>
    <script src="{{url('/assets/js/plugins/choices.min.js')}}"></script>
    <script src="{{url('/assets/js/plugins/quill.min.js')}}"></script>
    <script src="{{url('/assets/js/plugins/flatpickr.min.js')}}"></script>
    <script src="{{url('/assets/js/plugins/dropzone.min.js')}}"></script>
    <script>
      if (document.getElementById('editor')) {
        var quill = new Quill('#editor', {
          theme: 'snow' // Specify theme in configuration
        });
      }

      if (document.getElementById('choices-multiple-remove-button')) {
        var element = document.getElementById('choices-multiple-remove-button');
        const example = new Choices(element, {
          removeItemButton: true
        });

        example.setChoices(
          [{
              value: 'One',
              label: 'Label One',
              disabled: true
            },
            {
              value: 'Two',
              label: 'Label Two',
              selected: true
            },
            {
              value: 'Three',
              label: 'Label Three'
            },
          ],
          'value',
          'label',
          false,
        );
      }

      if (document.querySelector('.datetimepicker')) {
        flatpickr('.datetimepicker', {
          allowInput: true
        }); // flatpickr
      }

      Dropzone.autoDiscover = false;
      var drop = document.getElementById('dropzone')
      var myDropzone = new Dropzone(drop, {
        url: "/file/post",
        addRemoveLinks: true,
        acceptedFiles: [".zip",".rar",".7z"],
        maxFiles: 1,
      });
    </script>
    <!-- Kanban scripts -->
    <script src="{{url('/assets/js/plugins/dragula/dragula.min.js')}}"></script>
    <script src="{{url('/assets/js/plugins/jkanban/jkanban.js')}}"></script>
    <script>
      var win = navigator.platform.indexOf('Win') > -1;
      if (win && document.querySelector('#sidenav-scrollbar')) {
        var options = {
          damping: '0.5'
        }
        Scrollbar.init(document.querySelector('#sidenav-scrollbar'), options);
      }
    </script>
    <!-- Github buttons -->
    <script async defer src="https://buttons.github.io/buttons.js"></script>
    <!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
    <script src="{{url('assets/js/material-dashboard.min.js?v=3.1.0')}}"></script>
  </body>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            // Pega o link de Administração (o item dropdown)
            var adminLink = document.getElementById('adminLink');
            var adminIcon = document.getElementById('adminIcon');

            // Pega todos os links dentro do dropdown
            var dropdownLinks = document.querySelectorAll('.navbar-nav .dropdown-menu .dropdown-item');

            // Verifica a URL da página atual
            var currentUrl = window.location.href;

            // Adiciona a classe 'active bg-gradient-primary' ao link correspondente à página atual no dropdown
            dropdownLinks.forEach(function (link) {
                if (link.href === currentUrl) {
                    link.classList.add('active', 'bg-gradient-primary','text-white');
                    adminLink.classList.add('active', 'bg-gradient-primary');
                    adminIcon.classList.add('active', 'bg-gradient-primary');
                }
            });

            // Adiciona um ouvinte de evento de clique a cada link dentro do dropdown
            dropdownLinks.forEach(function (link) {
                link.addEventListener('click', function () {
                    // Remove a classe 'active bg-gradient-primary' de todos os links dentro do dropdown
                    dropdownLinks.forEach(function (dropdownLink) {
                        dropdownLink.classList.remove('active', 'bg-gradient-primary');
                    });

                    // Adiciona a classe 'active bg-gradient-primary' apenas ao link clicado no dropdown
                    link.classList.add('active', 'bg-gradient-primary','text-white');
                    adminLink.classList.add('active', 'bg-gradient-primary');
                    adminIcon.classList.add('active', 'bg-gradient-primary');
                });
            });
        });
    </script>
  <script>
    @if($page=="projeto")
  function redirecionar() {
      window.location.href = "{{ route('app.projeto_entrega') }}";
  }
  @endif
  @if($page=="Alunos")
  function redirecionar() {
      window.location.href = "{{route('app.main')}}";
  }
  function chooseFile() {
            document.getElementById('fileInput').click();
        }
  @endif
  @if($page=="Projeto")
  function redirecionar() {
      window.location.href = "{{route('projetos.index')}}";
  }
  @endif()
  </script>
  </html>
