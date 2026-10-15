<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'name',
        'email',
        'password',
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var array<int, string>
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * The attributes that should be cast.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'email_verified_at' => 'datetime',
        'password' => 'hashed',
    ];

    public function dadosPessoais()
    {
        // Determina o tipo de usuário e obtém o modelo correspondente
        $userType = $this->userType(); // Implemente o método userType conforme necessário

        // Retorna os dados pessoais correspondentes
        return $this->hasOne(DadosPessoais::class, 'id_acesso', $userType);
    }

    private function userType()
    {
        if ($this->aluno) {
            return 'aluno';
        } elseif ($this->admin) {
            return 'admin';
        } elseif ($this->docente) {
            return 'docente';
        } elseif ($this->empresa) {
            return 'empresa';
        }

        // Se nenhum tipo corresponder, você pode ajustar conforme necessário
        return 'aluno'; // Defina um padrão ou trate conforme necessário
    }

}
