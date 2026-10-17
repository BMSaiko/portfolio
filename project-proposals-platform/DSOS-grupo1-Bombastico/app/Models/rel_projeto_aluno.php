<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class rel_projeto_aluno extends Model
{

    use HasFactory;
    public $timestamps = false;

    protected $table='rel_projeto_aluno';

    protected $fillable=[
        'id_aluno','id_projeto','file'
    ];

    public function projetos()
    {
        return $this->belongsTo(Projeto::class,'id_projeto','id');
    }

    public function aluno(){
        return $this->belongsTo(aluno::class, 'id_Aluno', 'n_mecanografico');
    }
    
    public function feedbacks()
    {
        return $this->hasMany(rel_feedback_projeto::class, 'id_projeto', 'id_projeto');
    }

}
