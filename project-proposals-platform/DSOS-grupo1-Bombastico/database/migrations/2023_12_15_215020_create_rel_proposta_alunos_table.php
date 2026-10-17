<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::disableForeignKeyConstraints();

        Schema::create('rel_proposta_alunos', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('id_proposta')->unsigned();
            $table->foreign('id_proposta')->references('id')->on('propostas');
            $table->integer('id_Aluno')->unsigned();
            $table->foreign('id_Aluno')->references('n_mecanografico')->on('aluno');
            $table->integer('id_estado')->unsigned();
            $table->foreign('id_estado')->references('id')->on('estado_propostas_aluno');
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('rel_proposta_alunos');
    }
};
