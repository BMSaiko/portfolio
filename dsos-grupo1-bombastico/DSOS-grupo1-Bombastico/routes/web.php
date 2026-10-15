<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\IndexController;
use App\Http\Controllers\Login;
use App\Http\Controllers\CandidatarController;
use App\Http\Controllers\EmpresasController;
use App\Http\Controllers\PestiController;
use App\Http\Controllers\ResetPasswordController;
use Illuminate\Foundation\Auth\EmailVerificationRequest;
use Illuminate\Foundation\Auth\EmailVerificationNotification;
use Illuminate\Support\Facades\Password;
use App\Http\Controllers\ProjetoController;

use App\Http\Controllers\AlunoController;


/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "web" middleware group. Make something great!
|
*/


Route::name('app.')->middleware('auth')->group(function () {
    
    Route::get('/home', [IndexController::class, 'index'])->name('main');

    Route::get('/empresas', [IndexController::class, 'empresas'])->name('empresas');

    Route::get('/perfil', ([IndexController::class , 'profile']))->name('perfil');

    Route::get('/edit-profile', [IndexController::class, 'editProfile'])->name('edit.profile');

    Route::post('/update-profile', [IndexController::class, 'updateProfile'])->name('update.profile');
    Route::get('/alunos',([IndexController::class,'alunos']))->name('alunos');

    Route::get('/pesti', ([IndexController::class , 'pesti']))->name('pesti');

    Route::get('/projeto',([IndexController::class , 'projeto']))->name('projeto');

    //Route::get('/projeto/{id}',([ProjetoController::class , 'projeto']))->name('ver.projeto');

    Route::get('/mensagem',([IndexController::class, 'mensagem']))->name('mensagem');

    Route::get('/proposta/{id}',([IndexController::class, 'proposta']))->name('proposta');

    Route::post('/proposta/{id}/Candidatar',([CandidatarController::class,'index']))->name('Candidatar');

    Route::post('/proposta/{id}/Candidatar',([CandidatarController::class,'index']))->name('Candidatar');

    Route::post('/proposta/{id}/Candidatar-Docente', [CandidatarController::class, 'candidatarDocente'])->name('Candidatar-Docente');

    Route::get('/projeto/{id}',([IndexController::class,'projeto_entregar']))->name('projeto_apresentacao');

    Route::get('/projeto/{id}/entregar',([IndexController::class,'projeto_entregar_pasta']))->name('projeto_entrega');

    Route::post('/projeto/{id}/entregar/submit',([ProjetoController::class,'submeter_projeto']))->name('projeto_entregada');

    Route::post('proposta/{id}/aceitar_aluno',([CandidatarController::class,'aprovarPropostaaluno']))->name('aceitar_aluno_proposta');

    Route::post('proposta/{id}/rejeitar_aluno',([CandidatarController::class,'rejeitarPropostaaluno']))->name('rejeitar_aluno_proposta');

    Route::post('aluno/eliminar_aluno/{id}',([AlunoController::class,'Eliminar']))->name('aluno_eliminar');

    Route::post('/feedback/submit/{id_projeto}', [IndexController::class, 'submitFeedback'])->name('feedback.submit');});

Route::name('site.')->group(function(){
    Route::get('/',([Login::class,'index']))->name('login');

    Route::get('/registar',([Login::class,'registar']))->name('registar');

    Route::get('/lembrar',([Login::class,'lembrar']))->name('lembrar');

    Route::post('/lembrar',([Login::class,'authenticateLembrar']))->name('authenticateLembrar');

    Route::get('/login', [Login::class, 'index'])->name('login');

    Route::post('/login', [Login::class, 'authenticate'])->name('authenticate');

    Route::post('/submit_registar', [Login::class, 'register'])->name('naosei');

    Route::get('/logout', [Login::class, 'logout'])->name('logout');

    Route::post('/logout', [Login::class, 'logout'])->name('logout');
   
    Route::get('/password/reset/{token}/{email}', [ResetPasswordController::class, 'showResetForm'])
    ->name('password.reset');
    
    Route::post('/password/reset', [ResetPasswordController::class, 'reset'])
    ->name('password.update');

});

Route::name('empresas.')->middleware('auth')->group(function (){
    Route::get('/lista', [EmpresasController::class, 'index'])->name('lista');
    Route::put('/aprovar/{id}', [EmpresasController::class, 'aprovarEmpresa'])->name('aprovar');
    Route::put('/rejeitar/{id}', [EmpresasController::class, 'rejeitarEmpresa'])->name('rejeitar');
});

Route::name('pesti.')->middleware('auth')->group(function (){
    Route::get('/lista-pesti', [PestiController::class, 'index'])->name('index');
    Route::put('/propostas/{id}/aprovar', [PestiController::class, 'aprovarProposta'])->name('aprovar');
    Route::delete('/propostas/{id}/rejeitar', [PestiController::class, 'rejeitarProposta'])->name('rejeitar');
    Route::get('/criar-proposta-pesti', [PestiController::class, 'criarProposta'])->name('criar.proposta.pesti');
    Route::post('/salvar-proposta', [PestiController::class, 'salvarProposta'])->name('salvar.proposta');
});

Route::get('/reset-password/{token}/{email}', 'ResetPasswordController@showResetForm')->name('password.reset');
Route::post('/reset-password', 'ResetPasswordController@reset')->name('password.update');

Route::name('aluno.')->group(function(){
    Route::post('/aluno/upload',[AlunoController::class, 'upload'])->name('upload');
});

Route::name('projetos.')->middleware('auth')->group(function (){
    Route::get('/projetos/criar',[ProjetoController::class, 'index'])->name('index');
    Route::post('/projetos/criar/sucesso',[ProjetoController::class, 'criar'])->name('criar');
})
?>

