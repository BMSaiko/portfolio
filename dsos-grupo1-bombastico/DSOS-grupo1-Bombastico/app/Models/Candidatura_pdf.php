<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Candidatura_pdf extends Model
{
    use HasFactory;

    protected $table='rel_proposta_aluno_pdf';

    protected $fillable=[
        'id','id_proposta','id_Aluno','caminho_pdf'
    ];

    public function proposta(){
        return $this->belongsTo(Proposta::class,'id_proposta','id');
    }
    
    public function aluno(){
        return $this->belongsTo(User::class,'id_Aluno','n_mecanografico');
    }
}
