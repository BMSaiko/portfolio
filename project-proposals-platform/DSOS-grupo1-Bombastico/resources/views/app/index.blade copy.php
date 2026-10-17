@extends('app.template_app.template_index')
@section('main_content')
<div class="container-fluid py-4">
  <div class="row mt-4">
    <div class="col-lg-10 col-md-6 mb-md-0 mb-4 mx-auto">
      <div class="card">
        <div class="card-header pt-2">
            <div class="col-md-3">
          <a href="{{url('https://www.dei.isep.ipp.pt/')}}">
            <img src="{{url('assets/img/dep/DEI.png')}}" class="img-fluid shadow border-radius-lg w-35">
          </a>
        </div>
        <div class="col-md-12 col-xs-12 text-center">
            <h2 class="mt-n4">Sobre:</h2>
          </div>
        </div>
        <hr class="dark horizontal my-0">
        <div class="card-footer p-3">
          <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Veniam quaerat deleniti ipsam, vero, dicta exercitationem, laborum ratione officia ullam beatae placeat cupiditate. Odio, facere itaque voluptas illum nisi quaerat autem?</p>
        </div>
      </div>
    </div>
  </div>
  <div class="row mt-4">
    <div class="col-lg-10 col-md-6 mb-md-0 mb-4 mx-auto">
      <div class="card">
        <div class="card-header pb-0">
          <div class="row">
            <div class="col-lg-6 col-7">
              <h6>Empresas Para estagio:</h6>
              <p class="text-sm mb-0">
                <span class="font-weight-bold ms-1">Total de {{$t_proposta}} empresas</span>
              </p>
            </div>
          </div>
        </div>
        <div class="card-body px-0 pb-2">
          <div class="table-responsive">
            <table class="table align-items-center mb-0">
              <thead>
                <tr>
                  <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Titulo da Vaga</th>
                  <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Empresa</th>
                  <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Tipo de Posição</th>
                  <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Numero de vagas</th>
                </tr>
              </thead>
              <tbody>
                @foreach ($propostas as $proposta)
                <tr>
                  <td>
                    <div class="d-flex px-2 py-1">
                      <div>
                          <h6></h6>
                      </div>
                      <div class="d-flex flex-column justify-content-center">
                        <a href="{{route('app.proposta',$proposta->id)}}">
                          <h6 class="mb-0 text-sm">@if($proposta){{$proposta->Titulo}}@endif</h6>
                        </a>
                      </div>
                    </div>
                  </td>
                  <td>
                    <a href="https://www.nttdata.com/global/en/"><span class="mb-0 text-sm">{{$proposta->empresas->dados_pessoais->Nome}}</span></a>
                  </td>
                  <td class="align-middle text-center text-sm">
                    <span class="text-xs font-weight-bold">{{$proposta->Tipo_de_Trabalho}}</span>
                  </td>
                  <td class="align-middle text-center text-sm">
                    <h6 class="text-xs font-weight-bold">{{$proposta->vagas}}</h6>
                  </td>
                </tr>
                @endforeach
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
{{--     <div class="col-lg-4 col-md-6">
      <div class="card h-100">
        <div class="card-header pb-0">
          <h6>Orders overview</h6>
          <p class="text-sm">
            <i class="fa fa-arrow-up text-success" aria-hidden="true"></i>
            <span class="font-weight-bold">24%</span> this month
          </p>
        </div>
        <div class="card-body p-3">
          <div class="timeline timeline-one-side">
            <div class="timeline-block mb-3">
              <span class="timeline-step">
                <i class="material-icons text-success text-gradient">notifications</i>
              </span>
              <div class="timeline-content">
                <h6 class="text-dark text-sm font-weight-bold mb-0">$2400, Design changes</h6>
                <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">22 DEC 7:20 PM</p>
              </div>
            </div>
            <div class="timeline-block mb-3">
              <span class="timeline-step">
                <i class="material-icons text-danger text-gradient">code</i>
              </span>
              <div class="timeline-content">
                <h6 class="text-dark text-sm font-weight-bold mb-0">New order #1832412</h6>
                <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">21 DEC 11 PM</p>
              </div>
            </div>
            <div class="timeline-block mb-3">
              <span class="timeline-step">
                <i class="material-icons text-info text-gradient">shopping_cart</i>
              </span>
              <div class="timeline-content">
                <h6 class="text-dark text-sm font-weight-bold mb-0">Server payments for April</h6>
                <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">21 DEC 9:34 PM</p>
              </div>
            </div>
            <div class="timeline-block mb-3">
              <span class="timeline-step">
                <i class="material-icons text-warning text-gradient">credit_card</i>
              </span>
              <div class="timeline-content">
                <h6 class="text-dark text-sm font-weight-bold mb-0">New card added for order #4395133</h6>
                <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">20 DEC 2:20 AM</p>
              </div>
            </div>
            <div class="timeline-block mb-3">
              <span class="timeline-step">
                <i class="material-icons text-primary text-gradient">key</i>
              </span>
              <div class="timeline-content">
                <h6 class="text-dark text-sm font-weight-bold mb-0">Unlock packages for development</h6>
                <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">18 DEC 4:54 AM</p>
              </div>
            </div>
            <div class="timeline-block">
              <span class="timeline-step">
                <i class="material-icons text-dark text-gradient">payments</i>
              </span>
              <div class="timeline-content">
                <h6 class="text-dark text-sm font-weight-bold mb-0">New order #9583120</h6>
                <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">17 DEC</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div> --}}
</div>
@endsection