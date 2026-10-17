<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\Acesso;
use App\Models\Dados_pessoais;
use App\Models\aluno;
use App\Models\docente;
use App\Models\Admin;
use App\Models\Empresas;
use App\Models\User;
use App\Models\Pais;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Str;
use App\Mail\ResetPasswordEmail;

class Login extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('site.index');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function registar()
    {
        $paises = Pais::all();

        return view('site.signup', compact('paises'));
    }

    public function lembrar()
    {

        return view('site.lembrar');
    }

    public function authenticateLembrar(Request $request)
    {
        // 1. Verifica se o e-mail existe na base de dados
        $user = Dados_pessoais::where('Email', $request->input('Email'))->first();


        // 2. Gera e salva um token exclusivo para essa solicitação
        $token = Str::random(60); // Ou use Str::random(60) a partir do Laravel 7.x
        $createdAt = now(); // Data e hora atual

        // Atualiza ou insere o token na tabela password_reset_tokens
        DB::table('password_reset_tokens')->updateOrInsert(
            ['Email' => $user->Email, 'token' => $token, 'created_at' => $createdAt]
        );

        // 3. Envia um e-mail com o link de redefinição de senha
        $resetLink = route('site.password.reset', ['token' => $token, 'email' => $user->Email]);

        // Adicione a lógica para enviar o e-mail com o link de redefinição
        Mail::to($user->Email)->send(new ResetPasswordEmail($resetLink));

        return redirect()->back()->with('success', 'Um e-mail foi enviado com instruções para redefinir sua senha.');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function authenticate(Request $request)
{
    $request->validate([
        'username' => 'required|string',
        'password' => 'required|string',
    ]);

    $credentials = $request->only('username', 'password');

    if (Auth::attempt($credentials, $request->has('remember'))) {
        $user = Auth::user();

        // Obtém o ID do usuário logado na tabela "acesso"
        $userId = $user->id;

        // Verifica nas tabelas "aluno", "empresa" e "admin"
        $aluno = Aluno::where('id_acesso', $userId)->first();
        $empresa = Empresas::where('id_acesso', $userId)->first();
        $admin = Admin::where('id_acesso', $userId)->first();
        $docente = docente::where('id_acesso', $userId)->first();


        // Determine o tipo de usuário
        if ($aluno) {
            // O usuário é um aluno
            session(['user_type' => 'aluno']);
            session(['id_dados' => $aluno->id_dados]);
            return redirect()->route('app.main')->with('success', 'Dados válidos');
        } elseif ($empresa) {
            // O usuário é uma empresa
            session(['user_type' => 'empresa']);
            session(['id_dados' => $empresa->id_dados]);
            return redirect()->route('app.projeto')->with('success', 'Dados válidos');
        } elseif ($admin) {
            // O usuário é um admin
            session(['user_type' => 'admin']);
            session(['id_dados' => $admin->id_dados]);
            return redirect()->route('app.main')->with('success', 'Dados válidos');
        } elseif ($docente) {
            // Caso não seja nenhum dos tipos conhecidos
            session(['user_type' => 'docente']);
            session(['id_dados' => $docente->id_dados]);
            return redirect()->route('app.main')->with('success', 'Dados válidos');
        }else {
            // Caso não seja nenhum dos tipos conhecidos
            session(['user_type' => 'outro']);
        }

        return redirect()->route('app.main')->with('success', 'Dados válidos');
    } else {
        return redirect()->back()->with('error', 'Credenciais inválidas')->withInput();
    }
}


    public function logout()
    {
        Auth::logout();

        // Redireciona para a página de login após o logout
        return redirect()->route('site.login');
    }



    public function register(Request $request){
        
    // Validação dos dados recebidos do formulário
    
        
   
        // Criação do usuário na tabela 'acesso'
        $acesso = Acesso::create([
            'username' => $request->username,
            'password' => bcrypt($request->password),
        ]);

        $pais = Pais::where('Pais', $request->pais)->first();
        $id_acesso = $acesso ->getKey();


        // Criação dos dados pessoais na tabela 'dados_pessoais'
        $dadosPessoais = Dados_pessoais::create([
            'Nome' => $request->Nome,
            'Data_nascimento' => $request->Data_nascimento,
            'Email' => $request->Email,
            'NIF' => $request->NIF,
            'id_Pais' => $request->pais,
            'Ativo' => 'N',
            'Cidade' => $request->Cidade,
            'Informação' =>'Escreva alguma coisa aqui quando desejar!',
            'Telemovel' => $request->Telemovel,
            'image' => 'none',
        ]);

        $id_dados = $dadosPessoais ->getKey();

        $empresa = Empresas::create([
            'id_dados' => $id_dados,
            'id_acesso' => $id_acesso,
            'diretor' => $request->username,
            'aprovada' => '',            
        ]);

        info('Transação commitada com sucesso.');

        // Outras ações ou redirecionamentos após o registro
        return redirect()->route('app.main')->with('success', 'Empresa registrada com sucesso!');
    


        // Você pode adicionar logs ou mensagens de erro aqui
        return redirect()->back()->with('error', 'Erro ao registrar empresa. Por favor, tente novamente.')->withInput();
    
}


    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        // Pode ser vazia ou redirecionar para outro lugar
        return redirect()->route('site.login')  ;
    }


    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }
}
