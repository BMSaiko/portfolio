<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Empresas extends Model
{
    use HasFactory;

    public $timestamps = false;

    protected $table='empresa';
    protected $fillable=[
        'id_dados','id_acesso','diretor','image','aprovada'
    ];


    public function dados_pessoais(){
        return $this->belongsTo(Dados_pessoais::class,'id_dados','id');
    }

    public function acesso(){
        return $this->belongsTo(Acesso::class,'id_acesso','id');
    }

    public function propostas(){
        return $this->hasMany(propostas::class,'id_empresa','id');
    }
}
