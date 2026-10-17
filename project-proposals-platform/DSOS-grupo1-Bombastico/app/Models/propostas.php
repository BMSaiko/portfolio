<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class propostas extends Model
{
    use HasFactory;

    public $timestamps = false;

    protected $table = 'propostas';
    protected $fillable = [
        'id', 'id_empresa', 'Titulo', 'Descricao', 'Tipo_de_Trabalho', 'vagas', 'DataPublicacao', 'localização', 'id_estado'
    ];

    public function empresas()
    {
        return $this->belongsTo(Empresas::class, 'id_empresa', 'id');
    }

    public function estado_proposta()
    {
        return $this->belongsTo(estado_proposta::class, 'id_estado', 'id');
    }

    public function rel_proposta_aluno()
    {
        return $this->hasMany(rel_proposta_aluno::class, 'id_proposta', 'id');
    }

    public function candidatura_pdf()
    {
        return $this->hasMany(Candidatura_pdf::class, 'id_proposta', 'id');
    }

    public function rel_docente_proposta()
    {
        return $this->hasMany(rel_docente_proposta::class, 'id_proposta', 'id');
    }
}
