<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class rel_projetos_propostas extends Model
{
    use HasFactory;

    protected $table='rel_projetos_propostas';

    protected $fillable=[
        'id_proposta', 'id_projeto'
    ];

    public function projetos()
    {
        return $this->belongsTo(Projeto::class,'id_projeto','id');
    }

    public function propostas()
    {
        return $this->belongsTo(propostas::class,'id_proposta','id');
    }
}
