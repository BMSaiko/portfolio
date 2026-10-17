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

        Schema::create('aluno', function (Blueprint $table) {
            $table->increments('n_mecanografico');
            $table->integer('id_dados')->unsigned();
            $table->foreign('id_dados')->references('id')->on('dados__pessoais');
            $table->integer('id_acesso')->unsigned();
            $table->foreign('id_acesso')->references('id')->on('acesso');
            $table->string('Turma');
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('aluno');
    }
};
