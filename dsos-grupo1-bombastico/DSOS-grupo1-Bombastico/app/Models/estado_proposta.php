<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use App\Models\proposta;

class estado_proposta extends Model
{
    use HasFactory;

    protected $table='estado_proposta';

    protected $fillable=[
        'id','Estado'
    ];

    public function proposta(){
        return $this->hasMany(propostas::class,'id_estado','id');
    }
}
