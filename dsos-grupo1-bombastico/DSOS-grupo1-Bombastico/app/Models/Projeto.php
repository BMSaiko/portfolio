<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Projeto extends Model
{
    use HasFactory;

    protected $table='projetos';

    protected $fillable=[
        'id','Titulo','Descricao','Tipo_de_Trabalho','DataPublicacao','id_docente'
    ];

    public $timestamps = false;

    public function rel_projeto_aluno(){
        return $this->hasMany(rel_projeto_aluno::class,'id_projeto','id');
    }

    public function rel_projeto_proposta()
    {
        return $this->hasMany(rel_projetos_propostas::class,'id_projeto','id');

    }

    public function docente()
    {
        return $this->belongsTo(Docente::class, 'id_docente', 'id');
    }
}
