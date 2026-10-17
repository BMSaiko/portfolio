<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class rel_docente_proposta extends Model
{
    use HasFactory;

    public $timestamps = false;

    protected $table = 'rel_docente_proposta';
    protected $fillable = ['id_docente', 'id_proposta'];

    public function docente()
    {
        return $this->belongsTo(docente::class, 'id_docente', 'id');
    }

    public function proposta()
    {
        return $this->belongsTo(propostas::class, 'id_proposta', 'id');
    }
}
