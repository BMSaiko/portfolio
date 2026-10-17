<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class docente extends Model
{
    use HasFactory;

   protected $table='docentes';

   protected $fillable=[
    'id','id_dados','id_acesso','regente','ativo'
   ];

    public function dados_pessoais(){
        return $this->belongsTo(Dados_pessoais::class,'id_dados','id');
    }

    public function acesso() {
        return $this->belongsTo(Acesso::class,'id_acesso', 'id');
    }

    public function relDocenteProposta()
    {
        return $this->hasMany(rel_docente_proposta::class, 'id_docente', 'id');
    }

    public function projetos()
    {
        return $this->hasMany(Projeto::class, 'id_docente', 'id');
    }
}
