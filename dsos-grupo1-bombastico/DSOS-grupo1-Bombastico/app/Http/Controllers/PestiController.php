<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\propostas;
use App\Models\estado_proposta;
use App\Models\Dados_pessoais;
use App\Models\Empresas; 
use \App\Models\Acesso;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Storage;
use App\Mail\Email;
use App\Mail\Emailporaprovar;
use Illuminate\Support\Facades\Mail;

class PestiController extends Controller
{
    public function index()
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


    
    public function aprovarProposta($id)
    {
        $proposta = propostas::findOrFail($id);
        $estadoAdecorrer = estado_proposta::where('Estado', 'A decorrer')->first();
    
        if ($estadoAdecorrer) {
            $proposta->estado_proposta()->associate($estadoAdecorrer);
            $proposta->save();
    
        $idEmpresa = $proposta->id_empresa;

        // Buscar id_dados na tabela empresa
        $empresa = Empresas::findOrFail($idEmpresa);
        $idDados = $empresa->id_dados;

        // Buscar email na tabela dados_pessoais
        $dadosPessoais = Dados_pessoais::findOrFail($idDados);
        $emailDestinatario = $dadosPessoais->Email;

        // Enviar email
        Mail::to($emailDestinatario)->send(new Email($proposta, $emailDestinatario));

            return back()->with('success', 'Proposta aprovada com sucesso!');
        } else {
            return back()->with('error', 'O estado "A decorrer" não foi encontrado.');
        }
    }

    public function rejeitarProposta($id)
    {
        $proposta = propostas::findOrFail($id);
        $proposta->delete();

        return back()->with('success', 'Proposta rejeitada com sucesso!');
    }

    public function criarProposta()
    {
        return view('app.criar_proposta', [
            'app' => "Portal de PESTI",
            'page' => "criar_proposta"
        ]);
    }
    

    public function salvarProposta(Request $request)
{
    // Obtenha o usuário autenticado usando injeção de dependência
    $user = Auth::user();

    // Verifique se o usuário autenticado é uma instância de Acesso
    if ($user instanceof \App\Models\Acesso) {
        // Recupere o registro Empresas usando id_acesso
        $empresa = \App\Models\Empresas::where('id_acesso', $user->id)->first();

        if ($empresa) {
            // Crie uma nova instância de proposta
            $proposta = new \App\Models\propostas([
                'Titulo' => $request->input('titulo'),
                'Descricao' => $request->input('descricao'),
                'Tipo_de_Trabalho' => $request->input('tipo_de_trabalho'),
                'vagas' => $request->input('vagas'),
                'localização' => $request->input('localizacao'),
                'DataPublicacao' => now(),
            ]);

            // Associe a proposta com a empresa
            $proposta->id_empresa = $empresa->id;

            // Supondo que o estado padrão para uma nova proposta é 'Em Aprovação'
            $proposta->id_estado = \App\Models\estado_proposta::where('Estado', 'Em Aprovação')->first()->id;

            if ($request->hasFile('arquivo')) {
                // Obtenha o arquivo do formulário
                $arquivo = $request->file('arquivo');

                // Salve o arquivo PDF no armazenamento do Laravel e obtenha o caminho
                $caminhoArquivo = $arquivo->store('pdf', 'public');

                // Salve o caminho do arquivo no modelo da proposta
                $proposta->caminho_arquivo = $caminhoArquivo;
            }

            // Salve a proposta no banco de dados
            $proposta->save();

        $idEmpresa = $proposta->id_empresa;

        // Buscar id_dados na tabela empresa
        $empresa = Empresas::findOrFail($idEmpresa);
        $idDados = $empresa->id_dados;

        // Buscar email na tabela dados_pessoais
        $dadosPessoais = Dados_pessoais::findOrFail($idDados);
        $emailDestinatario = $dadosPessoais->Email;
        

        // Enviar email
        Mail::to("admin@gmail.com")->send(new Emailporaprovar($proposta, $emailDestinatario));

            // Redirecione para a página desejada após salvar
            return redirect()->route('app.perfil')->with('success', 'Proposta salva com sucesso!');
        } else {
            // Se não encontrar a empresa, use dd para parar a execução e mostrar informações
            dd('Empresa não encontrada para o usuário com id_acesso ' . $user->id);
        }
    } else {
        // Se o usuário não for uma instância de Acesso, use dd para mostrar informações
        dd('Usuário não é uma instância de Acesso');
    }
}
}