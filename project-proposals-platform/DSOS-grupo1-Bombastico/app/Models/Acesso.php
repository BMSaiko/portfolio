<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Auth\Authenticatable;
use Illuminate\Contracts\Auth\Authenticatable as AuthenticatableContract;
use App\Models\Dados_pessoais;

class Acesso extends Model implements AuthenticatableContract
{
    use HasFactory, Authenticatable;

    protected $table='acesso';
    protected $fillable=[
        'username','password'
    ];

    public $timestamps = false;

    public function empresas(){
        return $this->hasOne(Empresas::class,'id_acesso','id');
    }

    public function aluno(){
        return $this->hasOne(aluno::class, 'id_acesso', 'id');
    }

    public function docente(){
        return $this->hasOne(docente::class,'id_acesso','id');
    }

    public function dadosPessoais()
    {
        return $this->hasOne(\App\Models\Dados_pessoais::class, 'id_acesso', 'id');
    }

}
