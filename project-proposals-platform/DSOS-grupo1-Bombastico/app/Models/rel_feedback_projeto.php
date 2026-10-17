<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class rel_feedback_projeto extends Model
{
    use HasFactory;
    public $timestamps = false;

    protected $table='rel_feedback_projeto';

    protected $fillable=[
        'id_rel_projeto','feedback'
    ];

    public function relProjetoAluno()
    {
        return $this->belongsTo(rel_projeto_aluno::class, 'id_rel_projeto', 'id_projeto');
    }
}