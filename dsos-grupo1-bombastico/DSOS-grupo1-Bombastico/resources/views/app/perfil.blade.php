@extends('app.template_app.template_index')
@section('main_content')

<div class="container-fluid px-2 px-md-4">
    <div class="card card-body mx-3 mx-md-4 mt-2">
      <div class="row gx-4 mb-2">
        <div class="col-auto">
          <div class="avatar avatar-xl position-relative">
            <img src="{{url('assets/img/bruce-mars.jpg')}}" alt="profile_image" class="w-100 border-radius-lg shadow-sm">
          </div>
        </div>
        <div class="col-auto my-auto">
          <div class="h-100">
            <h5 class="mb-1">
              @php
              // Buscar id_dados da sessão
              $id_dados = session('id_dados');
              
              // Verificar se id_dados existe
              if ($id_dados) {
                  // Buscar nome na tabela dados_pessoais
                  $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                  
                  // Verificar se os dados foram encontrados
                  if ($dadosPessoais) {
                      echo $dadosPessoais->Nome;
                  } else {
                      echo 'Nome não encontrado';
                  }
              } else {
                  echo 'ID_dados não encontrado na sessão';
              }
            @endphp
            </h5>
          </div>
        </div>
        <div id="informacoes-pessoais-content">
            <div class="col-12 col-xl-6">
              <div class="card card-plain h-100">
                <div class="card-header pb-0 p-3">
                  <div class="row">
                    <div class="col-md-8 d-flex align-items-center">
                      <h6 class="mb-0">Informação</h6>
                      <a href="{{ route('app.edit.profile') }}">
                          <i class="fas fa-user-edit text-secondary text-sm p-2" data-bs-toggle="tooltip" data-bs-placement="top" title="Edit Perfil"></i>
                      </a>
                    </div>
                  </div>
                </div>
                <div class="card-body p-3">
                  <p class="text-sm">
                    @php
                    // Buscar id_dados da sessão
                    $id_dados = session('id_dados');
                    
                    // Verificar se id_dados existe
                    if ($id_dados) {
                        // Buscar nome na tabela dados_pessoais
                        $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                        
                        // Verificar se os dados foram encontrados
                        if ($dadosPessoais) {
                            echo $dadosPessoais->Informação;
                        } else {
                            echo 'Informacao não encontrado';
                        }
                    } else {
                        echo 'ID_dados não encontrado na sessão';
                    }
                  @endphp
                  </p>
                  <hr class="horizontal gray-light my-4">
                  <ul class="list-group">
                    <li class="list-group-item border-0 ps-0 pt-0 text-sm"><strong class="text-dark">Full Name:</strong> &nbsp;  @php
                      // Buscar id_dados da sessão
                      $id_dados = session('id_dados');
                      
                      // Verificar se id_dados existe
                      if ($id_dados) {
                          // Buscar nome na tabela dados_pessoais
                          $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                          
                          // Verificar se os dados foram encontrados
                          if ($dadosPessoais) {
                              echo $dadosPessoais->Nome;
                          } else {
                              echo 'Nome não encontrado';
                          }
                      } else {
                          echo 'ID_dados não encontrado na sessão';
                      }
                  @endphp</li>
                    <li class="list-group-item border-0 ps-0 text-sm"><strong class="text-dark">Mobile:</strong> &nbsp;  @php
                      // Buscar id_dados da sessão
                      $id_dados = session('id_dados');
                      
                      // Verificar se id_dados existe
                      if ($id_dados) {
                          // Buscar nome na tabela dados_pessoais
                          $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                          
                          // Verificar se os dados foram encontrados
                          if ($dadosPessoais) {
                              echo $dadosPessoais->Telemovel;
                          } else {
                              echo 'Nome não encontrado';
                          }
                      } else {
                          echo 'ID_dados não encontrado na sessão';
                      }
                  @endphp</li>
                    <li class="list-group-item border-0 ps-0 text-sm"><strong class="text-dark">Email:</strong> &nbsp;  @php
                      // Buscar id_dados da sessão
                      $id_dados = session('id_dados');
                      
                      // Verificar se id_dados existe
                      if ($id_dados) {
                          // Buscar nome na tabela dados_pessoais
                          $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                          
                          // Verificar se os dados foram encontrados
                          if ($dadosPessoais) {
                              echo $dadosPessoais->Email;
                          } else {
                              echo 'Nome não encontrado';
                          }
                      } else {
                          echo 'ID_dados não encontrado na sessão';
                      }
                  @endphp</li>
                    <li class="list-group-item border-0 ps-0 text-sm"><strong class="text-dark">Location:</strong> &nbsp;  @php
                      // Buscar id_dados da sessão
                      $id_dados = session('id_dados');
                      
                      // Verificar se id_dados existe
                      if ($id_dados) {
                          // Buscar nome na tabela dados_pessoais
                          $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                          
                          // Verificar se os dados foram encontrados
                          if ($dadosPessoais) {
                              echo $dadosPessoais->Cidade;
                          } else {
                              echo 'Nome não encontrado';
                          }
                      } else {
                          echo 'ID_dados não encontrado na sessão';
                      }
                  @endphp</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
    </div>
  @if(session('user_type') === 'empresa' || session('user_type') === 'aluno')
      @if(session('user_type') === 'empresa')
      @php
              
        // Obter o ID da empresa usando o ID de dados da sessão
        $empresaId = DB::table('empresa')->where('id_dados', session('id_dados'))->value('id');

        // Obter todas as propostas associadas a essa empresa
        $propostas = DB::table('propostas')->where('id_empresa', $empresaId)
        ->where('id_estado', 2)
        ->get();

        $id_dados = session('id_dados');
                      
        $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
      @endphp
      <div class="col-12">
            <a href="{{ route('pesti.criar.proposta.pesti') }}" class="btn btn-primary">Criar Proposta PESTI</a>
        </div>
    <div class="row mt-4">
      <div class="col-lg-10 col-md-6 mb-md-0 mb-4 mx-auto">
        <div class="card">
          <div class="card-header pb-0">
            <div class="row">
              <div class="col-lg-6 col-7">
                <h6>Propostas que ofereci:</h6>
                <p class="text-sm mb-0">
                  <span class="font-weight-bold ms-1">Total de {{count($propostas)}} empresas</span>
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
                            <h6 class="mb-0 text-sm">{{$proposta->Titulo}}</h6>
                          </a>
                        </div>
                      </div>
                    </td>
                    <td>
                      <a href="https://www.nttdata.com/global/en/"><span class="mb-0 text-sm">@php echo $dadosPessoais->Nome; @endphp</span></a>
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
              <div class="row mt-3">

    </div>
            </div>
          </div>
        </div>
      </div>
        @endif
        @if(session('user_type') === 'aluno')
          @php
          $n_mecanografico = \App\Models\aluno::where('id_dados', session('id_dados'))->value('n_mecanografico');
          $ids_propostas_aluno = \App\Models\rel_proposta_aluno::where('id_Aluno', $n_mecanografico)->pluck('id_proposta');
          $propostas_aluno = \App\Models\propostas::whereIn('id', $ids_propostas_aluno)->get();

          @endphp
        <div class="row mt-4">
          <div class="col-lg-10 col-md-6 mb-md-0 mb-4 mx-auto">
            <div class="card">
              <div class="card-header pb-0">
                <div class="row">
                  <div class="col-lg-6 col-7">
                    <h6>Empresas Para estagio:</h6>
                    <p class="text-sm mb-0">
                      <span class="font-weight-bold ms-1">Total de {{count($propostas_aluno)}} empresas</span>
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
                      @foreach ($propostas_aluno as $proposta)
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
            @endif
    @endif
      </div>
          {{-- 
            
            <div class="col-12 col-xl-4">
      <div class="card card-plain h-100">
        <div class="card-header pb-0 p-3">
          <h6 class="mb-0">Empresas que me candidatei</h6>
        </div>
        <div class="card-body p-3">
          <ul class="list-group">
            <li class="list-group-item border-0 d-flex align-items-center px-0 mb-2 pt-0">
              <div class="avatar me-3">
                <img src="../assets/img/kal-visuals-square.jpg" alt="kal" class="border-radius-lg shadow">
              </div>
              <div class="d-flex align-items-start flex-column justify-content-center">
                <h6 class="mb-0 text-sm">Sophie B.</h6>
                <p class="mb-0 text-xs">Hi! I need more information..</p>
              </div>
              <a class="btn btn-link pe-3 ps-0 mb-0 ms-auto w-25 w-md-auto" href="javascript:;">Reply</a>
            </li>
            <li class="list-group-item border-0 d-flex align-items-center px-0 mb-2">
              <div class="avatar me-3">
                <img src="../assets/img/marie.jpg" alt="kal" class="border-radius-lg shadow">
              </div>
              <div class="d-flex align-items-start flex-column justify-content-center">
                <h6 class="mb-0 text-sm">Anne Marie</h6>
                <p class="mb-0 text-xs">Awesome work, can you..</p>
              </div>
              <a class="btn btn-link pe-3 ps-0 mb-0 ms-auto w-25 w-md-auto" href="javascript:;">Reply</a>
            </li>
            <li class="list-group-item border-0 d-flex align-items-center px-0 mb-2">
              <div class="avatar me-3">
                <img src="../assets/img/ivana-square.jpg" alt="kal" class="border-radius-lg shadow">
              </div>
              <div class="d-flex align-items-start flex-column justify-content-center">
                <h6 class="mb-0 text-sm">Ivanna</h6>
                <p class="mb-0 text-xs">About files I can..</p>
              </div>
              <a class="btn btn-link pe-3 ps-0 mb-0 ms-auto w-25 w-md-auto" href="javascript:;">Reply</a>
            </li>
            <li class="list-group-item border-0 d-flex align-items-center px-0 mb-2">
              <div class="avatar me-3">
                <img src="../assets/img/team-4.jpg" alt="kal" class="border-radius-lg shadow">
              </div>
              <div class="d-flex align-items-start flex-column justify-content-center">
                <h6 class="mb-0 text-sm">Peterson</h6>
                <p class="mb-0 text-xs">Have a great afternoon..</p>
              </div>
              <a class="btn btn-link pe-3 ps-0 mb-0 ms-auto w-25 w-md-auto" href="javascript:;">Reply</a>
            </li>
            <li class="list-group-item border-0 d-flex align-items-center px-0">
              <div class="avatar me-3">
                <img src="../assets/img/team-3.jpg" alt="kal" class="border-radius-lg shadow">
              </div>
              <div class="d-flex align-items-start flex-column justify-content-center">
                <h6 class="mb-0 text-sm">Nick Daniel</h6>
                <p class="mb-0 text-xs">Hi! I need more information..</p>
              </div>
              <a class="btn btn-link pe-3 ps-0 mb-0 ms-auto w-25 w-md-auto" href="javascript:;">Reply</a>
            </li>
          </ul>
        </div>
      </div>
    </div>


            
            
            <div class="col-12 col-xl-4">
            <div class="card card-plain h-100">
              <div class="card-header pb-0 p-3">
                <h6 class="mb-0">Platform Settings</h6>
              </div>
              <div class="card-body p-3">
                <h6 class="text-uppercase text-body text-xs font-weight-bolder">Account</h6>
                <ul class="list-group">
                  <li class="list-group-item border-0 px-0">
                    <div class="form-check form-switch ps-0">
                      <input class="form-check-input ms-auto" type="checkbox" id="flexSwitchCheckDefault" checked>
                      <label class="form-check-label text-body ms-3 text-truncate w-80 mb-0" for="flexSwitchCheckDefault">Email me when someone follows me</label>
                    </div>
                  </li>
                  <li class="list-group-item border-0 px-0">
                    <div class="form-check form-switch ps-0">
                      <input class="form-check-input ms-auto" type="checkbox" id="flexSwitchCheckDefault1">
                      <label class="form-check-label text-body ms-3 text-truncate w-80 mb-0" for="flexSwitchCheckDefault1">Email me when someone answers on my post</label>
                    </div>
                  </li>
                  <li class="list-group-item border-0 px-0">
                    <div class="form-check form-switch ps-0">
                      <input class="form-check-input ms-auto" type="checkbox" id="flexSwitchCheckDefault2" checked>
                      <label class="form-check-label text-body ms-3 text-truncate w-80 mb-0" for="flexSwitchCheckDefault2">Email me when someone mentions me</label>
                    </div>
                  </li>
                </ul>
                <h6 class="text-uppercase text-body text-xs font-weight-bolder mt-4">Application</h6>
                <ul class="list-group">
                  <li class="list-group-item border-0 px-0">
                    <div class="form-check form-switch ps-0">
                      <input class="form-check-input ms-auto" type="checkbox" id="flexSwitchCheckDefault3">
                      <label class="form-check-label text-body ms-3 text-truncate w-80 mb-0" for="flexSwitchCheckDefault3">New launches and projects</label>
                    </div>
                  </li>
                  <li class="list-group-item border-0 px-0">
                    <div class="form-check form-switch ps-0">
                      <input class="form-check-input ms-auto" type="checkbox" id="flexSwitchCheckDefault4" checked>
                      <label class="form-check-label text-body ms-3 text-truncate w-80 mb-0" for="flexSwitchCheckDefault4">Monthly product updates</label>
                    </div>
                  </li>
                  <li class="list-group-item border-0 px-0 pb-0">
                    <div class="form-check form-switch ps-0">
                      <input class="form-check-input ms-auto" type="checkbox" id="flexSwitchCheckDefault5">
                      <label class="form-check-label text-body ms-3 text-truncate w-80 mb-0" for="flexSwitchCheckDefault5">Subscribe to newsletter</label>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div> --}}
 
@endsection