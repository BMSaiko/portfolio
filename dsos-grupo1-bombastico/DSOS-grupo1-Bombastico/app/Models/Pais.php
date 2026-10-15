<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Pais extends Model
{
    use HasFactory;

    protected $table = 'pais'; // ajuste se o nome da tabela for diferente
    protected $fillable = ['id','Pais']; // ajuste conforme os campos reais na tabela

}
