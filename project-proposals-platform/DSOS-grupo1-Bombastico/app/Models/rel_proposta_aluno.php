<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class rel_proposta_aluno extends Model
{
    use HasFactory;

    public $timestamps = false;

    protected $table='rel_proposta_alunos';
    protected $fillable=[
        'id_proposta','id_Aluno','id_estado'
    ];

    public function proposta(){
        return $this->belongsTo(proposta::class,'id_proposta','id');
    }

    public function aluno(){
        return $this->belongsTo(aluno::class, 'id_Aluno', 'n_mecanografico');
    }

    public function estado_proposta()
    {
        return $this->belongsTo('App\Models\estado_propostas_aluno', 'id_estado');
    }
}
