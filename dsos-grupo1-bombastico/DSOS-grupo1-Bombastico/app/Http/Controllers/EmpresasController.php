<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Empresas;

class EmpresasController extends Controller
{
    /**
     * Exibe a lista de Empresas.
     *
     * @return \Illuminate\View\View
     */
    public function index()
    {
        $Empresas = Empresas::where('aprovada', '')->with('dados_pessoais')->get();
        return view('app.empresas',[
            'app' => 'Portal de Pesti',
            'page' => 'empresas'
        ], compact('Empresas'));
    }
    

    /**
     * Exibe o formulário para criar uma nova Empresas.
     *
     * @return \Illuminate\View\View
     */
    public function create()
    {
        return view('empresas.create');
    }

    /**
     * Armazena uma nova Empresas na base de dados.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\RedirectResponse
     */
    public function store(Request $request)
    {
        // Validação dos dados do formulário aqui...

        Empresas::create([
            'id_dados' => $request->input('id_dados'),
            'id_acesso' => $request->input('id_acesso'),
            'diretor' => $request->input('diretor'),
            'image' => $request->input('image'),
            'aprovada' => $request->input('aprovada'),
        ]);

        return redirect()->route('empresas.index')->with('success', 'Empresas criada com sucesso!');
    }

    /**
     * Exibe os detalhes de uma Empresas específica.
     *
     * @param  \App\Models\Empresas  $Empresas
     * @return \Illuminate\View\View
     */
    public function show(Empresas $Empresas)
    {
        return view('empresas.show', compact('Empresas'));
    }

    /**
     * Exibe o formulário para editar uma Empresas específica.
     *
     * @param  \App\Models\Empresas  $Empresas
     * @return \Illuminate\View\View
     */
    public function edit(Empresas $Empresas)
    {
        return view('empresas.edit', compact('Empresas'));
    }

    /**
     * Atualiza os detalhes de uma Empresas na base de dados.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \App\Models\Empresas  $Empresas
     * @return \Illuminate\Http\RedirectResponse
     */
    public function update(Request $request, Empresas $Empresas)
    {
        // Validação dos dados do formulário aqui...

        $Empresas->update([
            'id_dados' => $request->input('id_dados'),
            'id_acesso' => $request->input('id_acesso'),
            'diretor' => $request->input('diretor'),
            'image' => $request->input('image'),
            'aprovada' => $request->input('aprovada'),
        ]);

        return redirect()->route('empresas.index')->with('success', 'Empresas atualizada com sucesso!');
    }

    /**
     * Remove uma Empresas da base de dados.
     *
     * @param  \App\Models\Empresas  $Empresas
     * @return \Illuminate\Http\RedirectResponse
     */
    public function destroy(Empresas $Empresas)
    {
        $Empresas->delete();

        return redirect()->route('empresas.index')->with('success', 'Empresas removida com sucesso!');
    }
    public function aprovarEmpresa($id) {
        $empresa = Empresas::findOrFail($id);
        $empresa->aprovada = "Sim";
        $empresa->save();
    
        return back()->with('success', 'Empresa aprovada com sucesso!');
    }
    
    
    public function rejeitarEmpresa($id) {
        $empresa = Empresas::findOrFail($id);
        $empresa->aprovada = "Nao";
        $empresa->save();
    
        return back()->with('success', 'Empresa rejeitada com sucesso!');
    }
    
    
}

