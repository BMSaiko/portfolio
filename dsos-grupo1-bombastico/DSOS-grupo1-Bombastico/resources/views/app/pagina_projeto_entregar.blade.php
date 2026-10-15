@extends('app.template_app.template_index')
@section('main_content')
    <div class="container-fluid py-4">
        <div class="row mt-4">
            <div class="col-lg-9 col-12 mx-auto position-relative">
                <div class="card">
                    <div class="card-body pt-2">
                        <h6 for="projectName">{{ $titulo }}</h6>
                       
                        <div class="row mt-4">
                            <div class="col-12 col-md-6">
                                <div class="form-group">
                                    <label>
                                        Descrição
                                    </label>
                                    <p class="form-text text-muted ms-1">
                                    {{ $descricao }}
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="row mt-4">
    <div class="text-center col-4">
        <label>Docente:</label>
        <p class="form-text text-muted ms-1">
        {{ $nomeDocente }}
        </p>
    </div>
    <div class="text-center col-4">
        <label>Tipo de trabalho:</label>
        <p class="form-text text-muted ms-1">
        {{ $tipo_trabalho }}
        </p>
    </div>
    <div class="text-center col-4">
        <label>Data de publicação:</label>
        <h6>
        {{ $dataPublicacao }}
        </h6>
    </div>
</div>




<div class="mt-4">
    <h6 class="mb-3">Feedbacks</h6>
    @forelse($feedbacks as $feedback)
        <div class="mb-3 pb-3 border-bottom">
            <p>{{ $feedback->feedback }}</p>
            <!-- Adicione outros campos de feedback conforme necessário -->
        </div>
    @empty
        <p>Sem feedbacks disponíveis.</p>
    @endforelse
</div>


@if(session('user_type') == 'docente')
<form action="{{ route('app.feedback.submit', ['id_projeto' => $id_projeto]) }}" method="post">

        @csrf
        <div class="d-flex justify-content-end mt-4">
            <input type="text" name="feedback" class="form-control border" placeholder="Digite seu feedback aqui">
        </div>
        <div class="d-flex justify-content-end mt-4">
            <button type="submit" class="btn bg-gradient-dark m-0 ms-2">Fazer Feedback</button>
        </div>
    </form>
@endif



@if(session('user_type') == 'aluno')
    <div class="d-flex justify-content-end mt-4">
        <button type="button" name="button" class="btn bg-gradient-dark m-0 ms-2" onclick="redirecionar()">Entregar Projeto</button>
    </div>
@endif

                        
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
