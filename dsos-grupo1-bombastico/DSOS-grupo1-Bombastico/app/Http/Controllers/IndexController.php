<?php

namespace App\Http\Controllers;

use App\Models\rel_feedback_projeto;
use Illuminate\Http\Request;
use App\Models\propostas;
use App\Models\aluno;
use App\Models\rel_proposta_aluno;
use App\Models\rel_docente_proposta;
use App\Models\rel_projeto_aluno;
use App\Models\Empresas;
use App\Models\docente;
use Illuminate\Support\Facades\Auth;
use Carbon\Carbon;
use App\Models\Dados_pessoais;
use Illuminate\View\View;
use App\Models\Pais;
use App\Http\Controllers\HomeController;
use App\Models\Projeto;

use Illuminate\Support\Facades\DB;

class IndexController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
{
    $propostas = Propostas::with(['empresas.dados_pessoais', 'estado_proposta'])
        ->whereHas('empresas', function ($query) {
            $query->where('aprovada', 'LIKE', 'S%');
        })
        ->where('id_estado', 2)
        ->get();

    $t_proposta = $propostas->count();

    return view('app.index', [
        'app' => "Portal de PESTI",
        'page' => "Propostas",
        't_proposta' => $t_proposta,
        'propostas' => $propostas
    ]);
}


    /**
     * Show the form for creating a new resource.
     */
    public function profile()
    {
        return view('app.perfil',[
            'app' => "Portal de PESTI",
            'page' => "perfil"
        ]);
    }

    public function perfil()
    {
        // Busque os dados do usuário para exibição no perfil
        $id_dados = session('id_dados');
        $dadosPessoais = Dados_pessoais::find($id_dados);

        return view('perfil', compact('dadosPessoais'));
    }


    public function mensagem()
    {
        return view('app.mensagem',[
            'app' => "PGPE",
            'page' => "mensagem"
        ]);
    }

    public function pesti()
    {
        // Modifique esta lógica conforme necessário para obter as propostas desejadas
        $propostas = propostas::whereHas('estado_proposta', function ($query) {
            $query->where('Estado', 'Em Aprovação');
        })->get();

        return view('app.pesti', [
            'app' => 'Portal de PESTI',
            'page' => 'pesti',
            'propostas' => $propostas,
        ]);
    }

    // Example in your controller
    public function editProfile(): View
    {
        // Busque os dados do usuário para exibição no formulário de edição
        $id_dados = session('id_dados');
        $dadosPessoais = Dados_pessoais::find($id_dados);
        $paises = Pais::all();

        return view('app.update-profile', [
            'app' => "Portal de PESTI",
            'page' => "update-profile"
        ], compact('paises'));
    }

    public function updateProfile(Request $request)
    {
        $request->validate([
            'nome' => 'required|string|max:255',
            'email' => 'required|email|max:255',
            'datanascimento' => 'required|date',
            'nif' => 'required|numeric',
            'pais' => 'required|exists:pais,id',
            'descricao' => 'required|string',
            'telemovel' => 'required|numeric',
            'localizacao' => 'required|string',
            // Adicione outras regras de validação conforme necessário
        ]);
    
        // Obtém o ID do usuário logado
        $id_dados = session('id_dados');
        $id_pais = $request->input('pais');

        // Atualiza os dados do perfil na base de dados
        $dadosPessoais = Dados_pessoais::find($id_dados);
        $dadosPessoais->Nome = $request->input('nome');
        $dadosPessoais->Email = $request->input('email');
        $dadosPessoais->Data_nascimento = $request->input('datanascimento');
        $dadosPessoais->NIF = $request->input('nif');
        $dadosPessoais->id_Pais = $id_pais;
        $dadosPessoais->Informação = $request->input('descricao');
        $dadosPessoais->Telemovel = $request->input('telemovel');
        $dadosPessoais->Cidade = $request->input('localizacao');
        // Adicione outros campos conforme necessário
    
        // Salva as alterações
        $dadosPessoais->save();

    return redirect()->route('app.perfil')->with('success', 'Profile updated successfully');
    }


    public function empresas()
    {
        $Empresas = Empresas::where('aprovada', '')->with('dados_pessoais')->get();
        return view('app.empresas',[
            'app' => 'Portal de PESTI',
            'page' => 'empresas'
        ], compact('Empresas'));
    }
    


    /**
     * Store a newly created resource in storage.
     */
    public function projeto()
    {
        $verificacao=aluno::join('acesso', 'aluno.id_acesso', '=', 'acesso.id')
        ->where('acesso.id', Auth::id())
        ->select('aluno.*') // Selecione todas as colunas da tabela Aluno
        ->first();
        //dd($verificacao);
        //dd($verificacao->n_mecanografico);

        $verificacao3 = docente::join('acesso','docentes.id_acesso','=','acesso.id')
        ->where('acesso.id',Auth::id())
        ->select('docentes.*')
        ->first();
        
        if($verificacao){
            $meus_projetos = Projeto::with(['rel_projeto_aluno.aluno'])
            ->whereHas('rel_projeto_aluno.aluno', function ($query) use ($verificacao) {
                $query->where('n_mecanografico', $verificacao->n_mecanografico);
            })
            ->get();



            $n_projetos = $meus_projetos->count();
            return view('app.projeto',[
                'app'        => "Portal de PESTI",
                'page'       => "Projeto",
                'usertype'   => "aluno",
                'n_projetos' => $n_projetos,
                'projetos'  => $meus_projetos
            ]);


        }else if($verificacao3){
            $meus_projetos = Projeto::where('id_docente',$verificacao3->id)->get();
            $n_projetos = $meus_projetos->count();
            return view('app.projeto',[
                'app'        => "Portal de PESTI",
                'page'       => "Projeto",
                'usertype'   => "docente",
                'n_projetos' => $n_projetos,
                'projetos'  => $meus_projetos
            ]);
        }else{
            $meus_projetos = Projeto::all();
            $n_projetos = $meus_projetos->count();
            return view('app.projeto',[
                'app'        => "Portal de PESTI",
                'page'       => "Projeto",
                'usertype'   => "docente",
                'n_projetos' => $n_projetos,
                'projetos'  => $meus_projetos
            ]);
        } 

    }

    

    /**
     * Display the specified resource.
     */


     public function proposta(string $id)
     {
         $proposta = propostas::with(['estado_proposta', 'empresas.Dados_pessoais', 'rel_proposta_aluno.aluno.Dados_pessoais'])
             ->where('id', $id)
             ->where('id_estado', '2')
             ->first();
     
         if (!$proposta) {
             //dd('Proposta não encontrada');
             return view('app.pagina_proposta', [
                 'app' => "Portal de PESTI",
                 'page' => "Projeto",
                 'usertype' => "aluno",
                 't_proposta' => 0,
                 'proposta' => null,
                 'id' => $id,
             ]);
         }
     
         $alunosInscritos = rel_proposta_aluno::with('aluno.Dados_pessoais')
             ->where('id_proposta', $id)
             ->get();
     
         $tipoUsuario = '';
     
         if (Auth::check()) {
             $user = Auth::user();
     
             if ($user instanceof \App\Models\Acesso) {
                 $empresa = Empresas::where('id_acesso', $user->id)->first();
                 $aluno = aluno::where('id_acesso', $user->id)->first();
                 $docente = docente::where('id_acesso', $user->id)->first();
     
                 if ($empresa) {
                     $tipoUsuario = 'empresa';
                 } elseif ($aluno) {
                     $tipoUsuario = 'aluno';
                 } elseif ($docente) {
                     $tipoUsuario = 'docente';
     
                     // Verificar se o docente já fez uma candidatura para a proposta
                     $candidaturaDocente = rel_docente_proposta::where('id_proposta', $id)->exists();
     
                     if ($candidaturaDocente) {
                         // Se já fez uma candidatura, redirecione ou faça o que for necessário
                         return view('app.pagina_proposta', [
                             'app' => "Portal de PESTI",
                             'page' => "proposta",
                             'usertype' => $tipoUsuario,
                             't_proposta' => 1,
                             'propostas' => $propostas,
                             'alunosInscritos' => $alunosInscritos,
                             't_alunos' => $alunosInscritos->count(),
                             'id' => $id,
                             'jaCandidatado' => true,
                         ]);
                     }
                 }
             }
         }
     
         return view('app.pagina_proposta', [
             'app' => "Portal de PESTI",
             'page' => "proposta",
             'usertype' => $tipoUsuario,
             't_proposta' => 1,
             'propostas' => $proposta,
             'alunosInscritos' => $alunosInscritos,
             't_alunos' => $alunosInscritos->count(),
             'id' => $id,
             'jaCandidatado' => false,
         ]);
     }
     







    /**
     * Update the specified resource in storage.
     */
    public function projeto_entregar(Request $request, int $id)
{
    $projeto = Projeto::with(['rel_projeto_aluno.aluno', 'docente'])
        ->where('id', $id)
        ->first();

    // Acessando os dados do projeto
    $titulo = $projeto->Titulo;
    $tipo_trabalho = $projeto->Tipo_de_Trabalho;
    $descricao = $projeto->Descricao;
    $dataPublicacao = $projeto->DataPublicacao;

    // Acessando os dados do docente
    $nomeDocente = $projeto->docente->dados_pessoais->Nome;

   // Obtendo o id_rel_projeto
   $feedbacks = rel_feedback_projeto::where('id_rel_projeto', $id)->get();

   


    //dd($feedbacks);

    //dd($feedbacks->feedback);
    // Outras lógicas podem ser adicionadas aqui, se necessário

    // Retorne os dados para a view ou faça o que for necessário
    return view('app.pagina_projeto_entregar', [
        'titulo' => $titulo,
        'app' => "Portal de PESTI",
        'page' => 'projeto_entregar',
        'id_projeto' => $id,
        'tipo_trabalho' => $tipo_trabalho,
        'descricao' => $descricao,
        'dataPublicacao' => $dataPublicacao,
        'feedbacks' => $feedbacks,  // Agora você tem uma coleção de feedbacks
        'nomeDocente' => $nomeDocente
    ]);
    
}





public function submitFeedback(Request $request, $id_projeto)
{
    // Validação dos dados do formulário (adicione mais regras conforme necessário)
    $request->validate([
        'feedback' => 'required|string|max:255',
    ]);

    $newFeedback = $request->input('feedback');

    DB::statement('SET FOREIGN_KEY_CHECKS=0');


    $relProjetoAluno = rel_feedback_projeto::create([
        'id_rel_projeto' => $id_projeto,
        'feedback' => $newFeedback,
        // Adicione outros campos conforme necessário
    ]);

    DB::statement('SET FOREIGN_KEY_CHECKS=1');

        // Redirecione de volta à página anterior com uma mensagem de sucesso
        return redirect()->back()->with('success', 'Feedback enviado com sucesso!');

}
    /**
     * Remove the specified resource from storage.
     */
    public function projeto_entregar_pasta(string $id)
    {
        return view('app.projeto-submissao',[
            'app'        => "Portal de PESTI",
            'page'       => "Projeto",
            't_proposta' => 0,
            'proposta'  => 0
        ]);
    }

    public function alunos()
    {
        //pega todos os alunos da base de dados
        $alunos=aluno::with(['Dados_pessoais'])->get();
        return view('app.alunos',[
            'app'   => "PESTI",
            'page'  => "Alunos",
            'alunos'=> $alunos
        ]);
    }
}
