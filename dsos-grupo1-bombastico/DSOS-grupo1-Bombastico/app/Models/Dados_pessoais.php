<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Dados_pessoais extends Model
{
    use HasFactory;

    public $timestamps = false;

    protected $table='dados_pessoais';
    protected $fillable=[
        'Nome','Data_nascimento','Email','NIF','id_Pais','Ativo','Cidade','Informação','Telemovel', 'image'
    ];

    public function empresas(){
        return $this->hasOne(Empresas::class,'id_dados','id');
    }

    public function aluno(){
        return $this->hasOne(aluno::class,'id_dados','id');
    }

    public function docente(){
        return $this->hasOne(docente::class,'id_dados','id');
    }
}
