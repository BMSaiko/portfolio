@extends('app.template_app.template_index')

@section('main_content')
    <div class="container mt-4">
        <form action="{{ route('pesti.salvar.proposta') }}" method="post" enctype="multipart/form-data">
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

            <div class="mb-3">
                <label for="vagas" class="form-label">Número de Vagas:</label>
                <input type="number" name="vagas" class="form-control border" required>
            </div>

            <!-- Add other fields as needed -->

            <div class="mb-3">
                <label for="localizacao" class="form-label">Localização:</label>
                <input type="text" name="localizacao" class="form-control border" required>
            </div>

            <div class="mb-3">
                <label for="arquivo" class="form-label">PDF da Proposta (opcional):</label>
                <input type="file" name="arquivo" class="form-control border" accept=".PDF">
            </div>            

            <button type="submit" class="btn btn-primary">Salvar Proposta</button>
        </form>
    </div>
@endsection
