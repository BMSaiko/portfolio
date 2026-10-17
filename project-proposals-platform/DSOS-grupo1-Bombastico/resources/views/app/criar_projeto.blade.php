@extends('app.template_app.template_index')

@section('main_content')
    <div class="container mt-4">
        <form action="{{ route('projetos.criar') }}" method="post" enctype="multipart/form-data">
            @csrf

            <div class="mb-3">
                <label for="titulo" class="form-label">Título:</label>
                <input type="text" name="titulo" class="form-control border" required>
            </div>

            <div class="mb-3">
                <label for="descricao" class="form-label">Descrição:</label>
                <textarea name="descricao" class="form-control border" required></textarea>
            </div>

            <div class="mb-3">
                <label for="tipo_de_trabalho" class="form-label">Tipo de Trabalho:</label>
                <input type="text" name="tipo_de_trabalho" class="form-control border" required>
            </div>     

            <button type="submit" class="btn btn-primary">Adicionar Projeto</button>
        </form>
    </div>
@endsection
