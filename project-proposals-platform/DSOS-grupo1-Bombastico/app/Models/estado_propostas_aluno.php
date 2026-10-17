<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class estado_propostas_aluno extends Model
{
    use HasFactory;

    protected $table = 'estado_propostas_aluno';

    // Se o campo de estado for diferente, ajuste o nome aqui
    protected $fillable = ['estado'];

    // Relacionamento reverso com rel_proposta_aluno
    public function relPropostaAluno()
    {
        return $this->hasMany('App\Models\rel_proposta_aluno', 'id_estado');
    }
}
