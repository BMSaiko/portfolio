<!-- resources/views/app/pesti.blade.php -->

@extends('app.template_app.template_index')
@section('main_content')
<div class="container-fluid py-4">
  <div class="row mt-4">
    <div class="col-lg-10 col-md-6 mb-md-0 mb-4 mx-auto">
      <div class="card">
        <div class="card-header pb-0">
          <h6>Propostas Para Estágio:</h6>
          <p class="text-sm">
            <span class="font-weight-bold ms-1">Total de Propostas</span>
          </p>
        </div>
        <div class="card-body px-0 pb-2">
          <div class="table-responsive">
            <table class="table align-items-center mb-0">
              <thead>
                <tr>
                  <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Empresa</th>
                  <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Tipo de Posição</th>
                  <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Número de Vagas</th>
                  <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Ação</th>
                </tr>
              </thead>
              <tbody>
                @foreach($propostas as $proposta)
                <tr>
                  <td>
                    <div class="d-flex px-2 py-1">
                      <div>
                        <img src="{{ url('assets/img/small-logos/logo-xd.svg') }}" class="avatar avatar-sm me-3" alt="xd">
                      </div>
                      <div class="d-flex flex-column justify-content-center">
                        <h6 class="mb-0 text-sm">{{ $proposta->empresas->dados_pessoais->Nome }}</h6>
                      </div>
                    </div>
                  </td>
                  <td class="align-middle text-center text-sm">
                    <span class="text-xs font-weight-bold">{{ $proposta->Tipo_de_Trabalho }}</span>
                  </td>
                  <td class="align-middle text-center text-xs font-weight-bold">
                    {{ $proposta->vagas }}
                  </td>
                  <td class="text-center">
                    <form action="{{ route('pesti.aprovar', ['id' => $proposta->id]) }}" method="POST" style="display: inline-block;">
                        @csrf
                        @method('PUT')
                        <button type="submit" class="btn btn-success btn-sm">Aprovar</button>
                    </form>
                    <form action="{{ route('pesti.rejeitar', ['id' => $proposta->id]) }}" method="POST" style="display: inline-block;">
                        @csrf
                        @method('DELETE')
                        <button type="submit" class="btn btn-danger btn-sm">Rejeitar</button>
                    </form>
                  </td>
                </tr>
                @endforeach
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</main>

<script>
  function aprovarProposta(id) {
    // Lógica de aprovação aqui...
    alert('Proposta aprovada!');
    // Você pode implementar a lógica adicional necessária aqui
  }

  function rejeitarProposta(id) {
    // Lógica de rejeição aqui...
    alert('Proposta rejeitada!');
    // Você pode implementar a lógica adicional necessária aqui
  }
</script>

@endsection
