<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\aluno;
use App\Models\Dados_pessoais;
use App\Models\Pais;
use App\Models\Acesso;

class AlunoController extends Controller
{
    public function AlterarDados(int $id){
        $aluno=aluno::find($id);
        return view('AlterarDados',compact('aluno'));
    }

    public function Eliminar(int $id){
        $aluno=aluno::find($id);
        $id_dados=aluno::where('n_mecanografico',$id)->select('id_dados')->first();
        $dados_pessoais=Dados_pessoais::where('id',$id_dados)->get();
        $dados_pessoais->ativo='N';
        return redirect()->back();
    }

    public function upload(Request $request){
        try{
        $file = $request->file('csv_file');


        $csvData = [];
        $handle = fopen($file, 'r');

        while (($row = fgetcsv($handle)) !== false) {
            $csvData[] = $row;
        }
        fclose($handle);
        $header = array_shift($csvData);        
        
        foreach ($csvData as $row) {
            if(count($row)>=12){
            list($nome,$data,$email,$nif,$Pais,$cidade,$Info,$Tele,$ima,$user,$pass,$Tur) = $row;
            $idPais = Pais::where('Pais', $Pais)->select('id')->first();
            $new_acesso= new Acesso;
            $new_acesso->username= $user;
            $new_acesso->password=$pass;
            $new_acesso->save();

            $id_acesso=$new_acesso->getKey();

                $new_dados = new Dados_pessoais;
                $new_dados->Nome=$nome;
                $new_dados->Data_nascimento=$data;
                $new_dados->Email=$email;
                $new_dados->NIF=$nif;
                $new_dados->id_Pais = $idPais ? $idPais->id : null;
                $new_dados->Ativo='S';
                $new_dados->Informação='';
                $new_dados->Cidade=$cidade;
                $new_dados->Telemovel=$Tele;
                $new_dados->image=$ima;
                $new_dados->save();
                
                
                $id_dados = $new_dados->getKey();
            
                
                $new_aluno= new aluno;
                $new_aluno->id_dados=$id_dados;
                $new_aluno->id_acesso=$id_acesso;
                $new_aluno->Turma=$Tur;
                $new_aluno->save();
            }
        }
        
        return redirect()->back()->with('success', 'Arquivo CSV processado com sucesso!');
    }catch(\Exception $e){
        return redirect()->back()->with('error', 'Erro ao processar o arquivo CSV. Detalhes: ' . $e->getMessage());}
    }
}
