<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class aluno extends Model
{
    use HasFactory;

    protected $table = 'aluno';

    protected $fillable = [
        'n_mecanografico',
        'id_dados',
        'id_acesso',
    ];

    public $timestamps = false;

    public function proposta(){
        return $this->hasMany(rel_proposta_aluno::class,'id_Aluno','n_mecanografico');
    }

    public function Dados_Pessoais(){
        return $this->belongsTo(Dados_pessoais::class, 'id_dados', 'id');
    }

    public function Acesso(){
        return $this->belongsTo(Acesso::class, 'id_acesso', 'n_mecanografico');
    }

    public function candidatura_pdf(){
        return $this->hasMany(Candidatura_pdf::class,'id_Aluno','n_mecanografico');
    }

    public function rel_projeto_aluno(){
        return $this->hasMany(rel_projeto_aluno::class,'id_Aluno','n_mecanografico');
    }
}
