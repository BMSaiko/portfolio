
@extends('app.template_app.template_index')
@section('main_content')
    <div class="container-fluid py-4">
      <div class="row mt-4">
        <div class="col-lg-9 col-12 mx-auto position-relative">
          <div class="card">
          <div class="card-header p-3 pt-2">
                        <div class="icon icon-lg icon-shape bg-gradient-dark shadow text-center border-radius-xl mt-n4 me-3 float-start">
                            <img src="default" alt="">
                        </div>
                        <h6 class="mb-0">@if($propostas){{$propostas->empresas->dados_pessoais->Nome}}@endif</h6>
                    </div>
                    <div class="card-body pt-2">
                        <h6 for="projectName">@if($propostas){{$propostas->Titulo}}@endif</h6>
                        <div class="row mt-4">
                            <div class="col-12 col-md-6">
                                <div class="form-group">
                                    <label>
                                        Descrição
                                    </label>
                                    <p class="form-text text-muted ms-1">
                                        @if($propostas){{$propostas->Descricao}}@endif
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="row mt-4">
                            <div class="text-center col-4">
                                <label>Tipo de Trabalho:</label>
                                <p class="form-text text-muted ms-1">
                                    @if($propostas){{$propostas->Tipo_de_Trabalho}}@endif
                                </p>
                            </div>
                            <!-- Adicione esta parte para exibir o nome do docente -->
                            <div class="text-center col-4">
                                <label>Vagas Restantes:</label>
                                <p class="form-text text-muted ms-1">
                                    @if($propostas){{$propostas->vagas}}@endif
                                </p>
                            </div>
                            <div class="text-center col-4">
                                <label>Data de publicação:</label>
                                <h6>
                                    @if($propostas){{$propostas->DataPublicacao}}@endif
                                </h6>
                            </div>
                        </div>
                        <div class="row mt-4">
    <div class="text-center col-12">
        <label>Docente Atribuído:</label>
        <p class="form-text text-muted ms-1">
            @if($propostas && $propostas->rel_docente_proposta && $propostas->rel_docente_proposta->isNotEmpty())
                {{ $propostas->rel_docente_proposta->first()->docente->Dados_pessoais->Nome }}
            @else
                Sem Docente Atribuído
            @endif
        </p>
    </div>
</div>

              <div class="row mt-4">
                <div class="col-lg-10 col-md-6 mb-md-0 mb-4 mx-auto">
                  <div class="card">
                    <div class="card-header pb-0">
                      <div class="row">
                        <div class="col-lg-6 col-7">
                          <h6>Alunos que inscreveram para estagio:</h6>
                          <p class="text-sm mb-0">
                            <span class="font-weight-bold ms-1">Total de {{$t_alunos}} alunos</span>
                          </p>
                        </div>
                      </div>
                    </div>
                    <div class="card-body px-0 pb-2">
                      <div class="table-responsive">
                        <table class="table align-items-center mb-0">
                          <thead>
                            <tr>
                              <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Nome</th>
                              <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Email</th>
                              <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Status</th>
                              @if(session('user_type') == 'empresa')
                              <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Resposta</th>
                              @endif
                            </tr>
                          </thead>
                          <tbody>
                          @foreach ($alunosInscritos ?? [] as $aluno)
  <tr>
      <td>
          <div class="d-flex px-2 py-1">
              <div>
                  <h6></h6>
              </div>
              <div class="d-flex flex-column justify-content-center">
                  <h6 class="mb-0 text-sm">{{ $aluno->aluno->dados_pessoais->Nome }}</h6>
              </div>
          </div>
      </td>
      <td>
          <a href="https://www.nttdata.com/global/en/">
              <span class="mb-0 text-sm">{{ $aluno->aluno->dados_pessoais->Email }}</span>
          </a>
      </td>
      <td class="align-middle text-center text-sm">
        <span class="text-xs font-weight-bold">{{ $aluno->estado_proposta->estado }}</span>
      </td>
      @if(session('user_type') == 'empresa')
      @if($aluno->estado_proposta->estado != 'Aprovado' && $aluno->estado_proposta->estado != 'Rejeitado')
      <td class="text-center">
        <form action="{{ route('app.aceitar_aluno_proposta', ['id' => $propostas->id]) }}" method="POST" style="display: inline-block;">
            @csrf
            @method('POST')
            <input type="hidden" name="aluno_email" value="{{ $aluno->aluno->dados_pessoais->Email }}">
            <button type="submit" class="btn btn-success btn-sm">Aprovar</button>
        </form>
        <form action="{{ route('app.rejeitar_aluno_proposta', ['id' => $propostas->id]) }}" method="POST" style="display: inline-block;">
            @csrf
            @method('POST')
            <input type="hidden" name="aluno_email" value="{{ $aluno->aluno->dados_pessoais->Email }}">
            <button type="submit" class="btn btn-danger btn-sm">Rejeitar</button>
        </form>
      </td>
      @endif
      @endif
  </tr>
  @endforeach
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </div>
              {{-- <label class="mt-4 form-label">Project Tags</label>
              <select class="form-control" name="choices-multiple-remove-button" id="choices-multiple-remove-button" multiple>
                <option value="Choice 1" selected>Choice 1</option>
                <option value="Choice 2">Choice 2</option>
                <option value="Choice 3">Choice 3</option>
                <option value="Choice 4">Choice 4</option>
              </select>
              <div class="row">
                <div class="col-6">
                  <div class="input-group input-group-static">
                    <label>Start Date</label>
                    <input class="form-control datetimepicker" type="text" data-input>
                  </div>
                </div>
                <div class="col-6">
                  <div class="input-group input-group-static">
                    <label>End Date</label>
                    <input class="form-control datetimepicker" type="text" data-input>
                  </div>
                </div>
              </div>
              <div class="input-group input-group-dynamic mt-4">
                <label class="form-label">Starting Files</label>
                <form action="/file-upload" class="form-control dropzone" id="dropzone">
                  <div class="fallback">
                    <input name="file" type="file" multiple />
                  </div>
                </form>
              </div> --}}
              <div class="d-flex justify-content-end mt-4">
                                
                                
                                @if($usertype === 'docente' && !$jaCandidatado)
                                <form action="{{ route('app.Candidatar-Docente', $id) }}" method="post">
                                    @csrf
                                    <button type="submit" name="button" class="btn bg-gradient-dark m-0 ms-2">Candidatar</button>
                                </form>
                                @endif
                            </div>
                
                  @if(session('user_type') == 'aluno')
                  @php
                     $idDados = session('id_dados');
                     $aluno = \App\Models\aluno::where('id_dados', $idDados)->first();
                $jaInscrito = \App\Models\rel_proposta_aluno::where('id_aluno', $aluno->n_mecanografico)
                            ->where('id_proposta', $propostas->id)
                            ->exists();
        @endphp
        @if(!$jaInscrito)
                  <form action="{{ route('app.Candidatar', ['id' => $proposta->id]) }}" method="post">
                    @csrf
                  <button type="submit" name="button" class="btn bg-gradient-dark m-0 ms-2">Inscrever</button>
                </form>
                @endif
                  @endif
              </div>
            </div>
          </div>
        </div>
      </div>
    @endsection