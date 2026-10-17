<?php

namespace App\Http\Controllers;

use App\Models\rel_projeto_aluno;
use App\Models\rel_projetos_propostas;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\DB;
use App\Models\Projeto;
use App\Models\docente;
use Illuminate\Support\Facades\Auth;

class ProjetoController extends Controller
{
    public function submeter_projeto(int $id,Request $request){

        $file=$request->file('file');
        $fileName=Auth::user()->nome."propostaid".$id;
        $verificar = Projeto::join('rel_projeto_aluno','rel_projeto_aluno.id_projeto','=','Projeto.id')
        ->where('Projeto.id',$id)
        ->where('rel_projeto_aluno',Auth::id())
        ->first();
        $Pathfile="public/projetos/".$empresa->dados_pessoais()->nome;
        if($verificar){
            if(!Storage::exists($Pathfile)){
                Storage::makeDirectory($Pathfile);
                Storage::makeDirectory($Pathfile . '/proposta-id' .$id);
            }
            $Pathfile= $Pathfile . '/proposta-id' .$id;
            if(!Storage::exists($Pathfile)){
                Storage::makeDirectory($Pathfile);
            }
            if(Storage::disk($fileName)->exists($Pathfile)){
                File::delete($Pathfile.'/'.$fileName);
                $file->move($Pathfile,$fileName);
            }else{
                $file->move($Pathfile,$fileName);
            }
        }

        $Pathfile= $Pathfile.'/'.$fileName;

        $nova_submissao=new rel_projeto_aluno();
        $nova_submissao->id_aluno=Auth::id();
        $nova_submissao->id_projeto=$id;
        $nova_submissao->file=$Pathfile;
        $nova_submissao->save();

        return redirect()->route('app.main');
    }

    public function index(){
        $verificacao = docente::join('acesso','docentes.id_acesso','=','acesso.id')->where('acesso.id',Auth::id())->select('docentes.*')->first();

        return view('app.criar_projeto',[
            'app'=>"Portal de Pesti",
            'page'=>"Criar Projeto"
        ]);
    }

    public function criar(Request $request){
        try{ 
            $stringSemVirgulas = str_replace(',', ' ', $request->alunos);
            $arrayResultado = explode(' ', $stringSemVirgulas);

            $id_dados = docente::where('id_acesso', Auth::id())->first();
           
            $novo_projeto= new Projeto;
            $novo_projeto->Titulo=$request->titulo;
            $novo_projeto->Descricao=$request->descricao;
            $novo_projeto->Tipo_de_Trabalho=$request->tipo_de_trabalho;
            $novo_projeto->DataPublicacao=now();
            $novo_projeto->id_docente=$id_dados->id;
            $novo_projeto->save();
        
            $id_projeto=$novo_projeto->getKey();
            

            //foreach($arrayResultado as $resultado){
            //$ajustar_aluno = new rel_projeto_aluno;
            //$ajustar_aluno->id_aluno=$resultado;
            //$ajustar_aluno->id_projeto=$id_projeto;
            //$ajustar_aluno->file=" ";
            //$ajustar_aluno->save();
            //}
            return redirect()->route('app.projeto')->with('success', 'Arquivo CSV processado com sucesso!');
        }catch(\Exception $e){
            return redirect()->route('app.projeto')->with('error', 'Erro ao processar o arquivo CSV. Detalhes: ' . $e->getMessage());}
    }

    
}
