@extends('app.template_app.template_index')
@section('main_content')
<div class="container-fluid py-4">
  <!-- Restante do seu código permanece inalterado -->

  <div class="row mt-4">
    <div class="col-lg-10 col-md-6 mb-md-0 mb-4 mx-auto">
        <form action="{{route('aluno.upload')}}" method="post" enctype="multipart/form-data" id="uploadForm">
            @csrf
            <input type="file" name="csv_file" id="fileInput" accept=".csv" style="display: none;">
            <button type="button"  class="btn btn-grey btn-sm" onclick="chooseFile()">Escolher Arquivo</button>
            <button type="submit" class="btn btn-success btn-sm">Enviar</button>
        </form>
      <div class="card">
        <div class="card-header pb-0">
          <h6>Alunos Para Estágio:</h6>
          <p class="text-sm">
            <span class="font-weight-bold ms-1">Total de Alunos</span>
          </p>
        </div>
        <div class="card-body px-0 pb-2">
          <div class="table-responsive">
            <table class="table align-items-center mb-0">
              <!-- Cabeçalho da Tabela -->
              <thead>
                <tr>
                  <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Nome</th>
                  <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Turma</th>
                  <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Ação</th>
                </tr>
              </thead>
              <tbody>
                <!-- Linhas da Tabela -->
                @foreach($alunos as $aluno)
                <tr>
                    <!-- Dados do aluno -->
                    <td>
                        <div class="d-flex px-2 py-1">
                            <div>
                                <img src="{{ url('assets/img/small-logos/logo-xd.svg') }}" class="avatar avatar-sm me-3" alt="xd">
                            </div>
                            <div class="d-flex flex-column justify-content-center">
                                <h6 class="mb-0 text-sm">{{ $aluno->dados_pessoais->Nome }}</h6>
                            </div>
                        </div>
                    </td>
                    <!-- Turma do Aluno -->
                    <td>
                        <div class="avatar-group mt-2">
                            <span class="nav-link-text ms-1">{{ $aluno->Turma }}</span>
                        </div>
                    </td>
                    <!-- Ação de Alterar dados/Eliminar -->
                    <td class="text-center">
                            <button type="submit" class="btn btn-success btn-sm" onclick="redirecionar()">Alterar dados de Aluno</button>

                        <form action="{{route('app.aluno_eliminar', ['id' => $aluno->n_mecanografico])}}" method="POST">
                            @csrf
                            @method('PUT')
                            <button type="submit" class="btn btn-danger btn-sm">Eliminar Aluno</button>
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

<!-- Script para Aprovar/Rejeitar aluno -->
<!-- Remova os scripts de aprovação e rejeição, pois você está usando formulários para essas ações. -->
@endsection
