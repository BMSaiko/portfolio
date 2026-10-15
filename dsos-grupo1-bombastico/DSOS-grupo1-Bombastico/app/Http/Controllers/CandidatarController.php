<?php

namespace App\Http\Controllers;

use App\Models\estado_propostas_aluno;
use App\Models\Dados_pessoais;
use App\Models\aluno;
use App\Models\rel_proposta_aluno;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\rel_docente_proposta;

class CandidatarController extends Controller
{
    public function index(int $id,Request $request)
    {   
        $id_acesso = Auth::id();

        $proposta = \App\Models\Propostas::findOrFail($id);

        $aluno = \App\Models\Aluno::where('id_acesso', $id_acesso)->first();
        if ($proposta->vagas > 0) {
        $nova_Candidatura=new rel_proposta_aluno;
        $nova_Candidatura->id_proposta = $id;
        $nova_Candidatura->id_Aluno = $aluno->n_mecanografico;
        $nova_Candidatura->id_estado = 1;
        $nova_Candidatura->save();

        $proposta->decrement('vagas');

        return redirect()->route('app.proposta',$id);
    } else {
        // Não há vagas disponíveis, você pode lidar com isso conforme necessário
        return redirect()->route('app.proposta', $id)->with('error', 'Não há vagas disponíveis.');
    }
    }

    public function aprovarPropostaaluno($id, Request $request)
    {
        $email = $request->input('aluno_email');
        $dadosPessoais = Dados_pessoais::where('Email', $email)->first();
        $aluno = Aluno::where('id_dados', $dadosPessoais->id)->first();
        $rel_proposta_aluno = rel_proposta_aluno::where('id_proposta', $id)->first()
        ->where('id_aluno', $aluno->n_mecanografico) // Assuming the authenticated user is the aluno
        ->where('id_estado', 1) // Assuming 1 is the id for 'Em Espera' state
        ->first();

        if ($rel_proposta_aluno) {

                $rel_proposta_aluno->id_estado = 2; // Set id_estado to 2
                $rel_proposta_aluno->save();
                return back()->with('success', 'Proposta aprovada com sucesso!');
        } else {
            return back()->with('error', 'Relação proposta-aluno não encontrada ou estado incorreto.');
        }
    }

    public function rejeitarPropostaaluno($id, Request $request)
    {
        $email = $request->input('aluno_email');
        $dadosPessoais = Dados_pessoais::where('Email', $email)->first();
        $aluno = Aluno::where('id_dados', $dadosPessoais->id)->first();
        $rel_proposta_aluno = rel_proposta_aluno::where('id_proposta', $id)->first()
        ->where('id_aluno', $aluno->n_mecanografico) // Assuming the authenticated user is the aluno
        ->where('id_estado', 1) // Assuming 1 is the id for 'Em Espera' state
        ->first();

        if ($rel_proposta_aluno) {

                $rel_proposta_aluno->id_estado = 3; // Set id_estado to 2
                $rel_proposta_aluno->save();
                return back()->with('success', 'Proposta aprovada com sucesso!');
        } else {
            return back()->with('error', 'Relação proposta-aluno não encontrada ou estado incorreto.');
        }
    }

    public function aceitar(int $id,int $id_aluno,Request $request)
    {
        $relacao=rel_proposta_aluno::where('id_Aluno',$id_aluno)->where('id_proposta',$id)->first();
            $verifica=proposta::with(['estado_proposta','rel_proposta_aluno.aluno.Dados_pessoais'])
            ->where('proposta.id',$id)
            ->where('proposta.id_estado','2')
            ->where('rel_proposta_alunos.id_Aluno',$id_aluno)
            ->get();
            $relacao=rel_proposta_aluno::where('id_Aluno',$id_aluno)->where('id_proposta',$id)->first();

        $button=$request->has("Admitir");
        $button_reprovar= $request->has("Reprovar");


        if(isset($button)) {
            if(!$verifica->isEmpty()){
                $relacao->id_estado='2';
                $relacao->save();
                return redirect()->back();
            }else{
                return redirect()->route('app.main');
            }
        }else if(isset($button_reprovar)){
            if(!$verifica->isEmpty()){
                $relacao->id_estado='3';
                $relacao->save();
                return redirect()->back();
            }else{
                return redirect()->route('app.main');
            }
        }
    }

    public function candidatarDocente(int $id, Request $request)
{
    // Obter o ID do docente autenticado
    $user = Auth::user();
    
    $id_docente = "";
    
    if ($user instanceof \App\Models\Acesso){
        // Corrigir: Obter apenas o valor do campo 'id' da tabela 'docentes'
        $id_docente = \App\Models\docente::where('id_acesso', $user->id)->value('id');
        //dd($id_docente);
        if($id_docente){
            // Verificar se o docente já está associado à proposta
            //$relacaoDocenteProposta = rel_docente_proposta::where('id_docente', $id_docente)
            //    ->where('id_proposta', $id)
            //    ->first();

            //if ($relacaoDocenteProposta) {
            //    // Se já estiver associado, redirecione de volta à página da proposta
            //    return redirect()->route('app.proposta', $id);
            //} else {
                // Criar uma nova relação entre docente e proposta
                $novaRelacaoDocenteProposta = new rel_docente_proposta();
                $novaRelacaoDocenteProposta->id_docente = $id_docente;
                $novaRelacaoDocenteProposta->id_proposta = $id;
                $novaRelacaoDocenteProposta->save();

                // Redirecionar de volta à página da proposta
                return redirect()->route('app.proposta', $id);
            //}
        }
    } else {
        dd($id_docente);
    }
}

}