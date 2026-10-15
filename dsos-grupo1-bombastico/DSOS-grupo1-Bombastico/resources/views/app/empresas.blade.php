@extends('app.template_app.template_index')
@section('main_content')
<div class="container-fluid py-4">
  <!-- Restante do seu código permanece inalterado -->

  <div class="row mt-4">
    <div class="col-lg-10 col-md-6 mb-md-0 mb-4 mx-auto">
      <div class="card">
        <div class="card-header pb-0">
          <h6>Empresas Para Estágio:</h6>
          <p class="text-sm">
            <span class="font-weight-bold ms-1">Total de Empresas</span>
          </p>
        </div>
        <div class="card-body px-0 pb-2">
          <div class="table-responsive">
            <table class="table align-items-center mb-0">
              <!-- Cabeçalho da Tabela -->
              <thead>
                <tr>
                  <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Empresa</th>
                  <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Diretor</th>
                  <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Ação</th>
                </tr>
              </thead>
              <tbody>
                <!-- Linhas da Tabela -->
                @foreach($Empresas as $empresa)
                <tr>
                    <!-- Dados da Empresa -->
                    <td>
                        <div class="d-flex px-2 py-1">
                            <div>
                                <img src="{{ url('assets/img/small-logos/logo-xd.svg') }}" class="avatar avatar-sm me-3" alt="xd">
                            </div>
                            <div class="d-flex flex-column justify-content-center">
                                <h6 class="mb-0 text-sm">{{ $empresa->dados_pessoais->Nome }}</h6>
                            </div>
                        </div>
                    </td>
                    <!-- Diretor da Empresa -->
                    <td>
                        <div class="avatar-group mt-2">
                            <a href="javascript:;" class="avatar avatar-xs rounded-circle" data-bs-toggle="tooltip" data-bs-placement="bottom" title="{{ $empresa->diretor }}">
                                <img src="{{ url('assets/img/team-1.jpg') }}" alt="{{ $empresa->diretor }}">
                            </a>
                        </div>
                    </td>
                    <!-- Ação de Aprovação/Rejeição -->
                    <td class="text-center">
                        <form action="{{ route('empresas.aprovar', ['id' => $empresa->id]) }}" method="POST">
                            @csrf
                            @method('PUT')
                            <button type="submit" class="btn btn-success btn-sm">Aprovar</button>
                        </form>

                        <form action="{{ route('empresas.rejeitar', ['id' => $empresa->id]) }}" method="POST">
                            @csrf
                            @method('PUT')
                            <button type="submit" class="btn btn-danger btn-sm">Rejeitar</button>
                        </form>
                    </td>
                </tr>
                @endforeach

                <!-- Adicione mais linhas conforme necessário -->

              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Restante do seu código permanece inalterado -->

</div>
</main>

<!-- Script para Aprovar/Rejeitar Empresa -->
<!-- Remova os scripts de aprovação e rejeição, pois você está usando formulários para essas ações. -->
@endsection
