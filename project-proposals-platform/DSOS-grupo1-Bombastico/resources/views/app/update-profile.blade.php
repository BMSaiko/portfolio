@extends('app.template_app.template_index')

@section('main_content')
    <div class="container mt-4">
        <form action="{{ route('app.update.profile') }}" method="post">
            @csrf
            @if(session('user_type') != 'aluno')
            <div class="mb-3">
                <label for="nome" class="form-label">Nome:</label>
                <input type="text" name="nome" value="@php
                // Adicione a lógica para buscar e exibir o nome do usuário
                $id_dados = session('id_dados');
                $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                echo $dadosPessoais->Nome;
                @endphp" class="form-control border" required>
            </div>

            <div class="mb-3">
                <label for="email" class="form-label">Email:</label>
                <input type="email" name="email" value="@php
                // Adicione a lógica para buscar e exibir o nome do usuário
                $id_dados = session('id_dados');
                $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                echo $dadosPessoais->Email;
                @endphp" class="form-control border" required>
            </div>

            <div class="mb-3">
                <label for="datanascimento" class="form-label">Data de Nascimento:</label>
                <input type="date" name="datanascimento" value="@php
                // Adicione a lógica para buscar e exibir o nome do usuário
                $id_dados = session('id_dados');
                $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                echo $dadosPessoais->Data_nascimento;
                @endphp" class="form-control border" required>
            </div>

            <div class="mb-3">
                <label for="nif" class="form-label">NIF:</label>
                <input type="number" name="nif" value="@php
                // Adicione a lógica para buscar e exibir o nome do usuário
                $id_dados = session('id_dados');
                $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                echo $dadosPessoais->NIF;
                @endphp" class="form-control border" required>
            </div>

            <div class="mb-3">
              <label for="pais" class="form-label">Pais:</label>
              <select name="pais" class="form-control border">
                @foreach ($paises as $pais)
                    <option value="{{ $pais->id }}"> {{ $pais->Pais }} </option>
                @endforeach
            </select>
            </div>

            <div class="mb-3">
              <label for="descricao" class="form-label">Descrição:</label>
              <input type="text" name="descricao" value="@php
              // Adicione a lógica para buscar e exibir o nome do usuário
              $id_dados = session('id_dados');
              $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
              echo $dadosPessoais->Informação;
              @endphp" class="form-control border" required>
            </div>

            <div class="mb-3">
              <label for="telemovel" class="form-label">Telemovel:</label>
              <input type="number" name="telemovel" value="@php
              // Adicione a lógica para buscar e exibir o nome do usuário
              $id_dados = session('id_dados');
              $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
              echo $dadosPessoais->Telemovel;
              @endphp" class="form-control border" required>
            </div>

            <!-- Add other fields as needed -->

            <div class="mb-3">
                <label for="localizacao" class="form-label">Localização:</label>
                <input type="text" name="localizacao" value="@php
                // Adicione a lógica para buscar e exibir o nome do usuário
                $id_dados = session('id_dados');
                $dadosPessoais = \App\Models\Dados_pessoais::find($id_dados);
                echo $dadosPessoais->Cidade;
                @endphp" class="form-control border" required>
            </div>

            <div class="mb-3">
              <label for="username" class="form-label">Username:</label>
              <input type="text" name="username"value="@php
              // Adicione a lógica para buscar e exibir o nome do usuário
              $id_dados = session('id_dados');

              if(session('user_type') == 'empresa'){
                $aluno = \App\Models\Empresas::where('id_dados', $id_dados)->first();

                if($aluno){
                  $id_acesso = $aluno->id_acesso;

                  $acesso = \App\Models\Acesso::find($id_acesso);
                  if($acesso){
                    $username = $acesso->username;
                    echo $username;
                  }
                  else{
                    echo 'Acesso nao';
                  }
                }
                else{
                  echo 'aluno nao';
                }
              }

              
              @endphp" class="form-control border" required>
          </div>
          @endif

          
          <div class="mb-3">
              <label for="password" class="form-label">Palavra-passe:</label>
              <input type="text" name="password" class="form-control border" required>
          </div>

            <button type="submit" class="btn btn-primary">Salvar Perfil</button>
        </form>
    </div>
@endsection
