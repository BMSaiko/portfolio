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

        Schema::create('dados_pessoais', function (Blueprint $table) {
            $table->increments('id');
            $table->string('Nome');
            $table->date('Data_nascimento');
            $table->string('Email')->unique();
            $table->integer('NIF');
            $table->integer('id_Pais')->unsigned();
            $table->foreign('id_Pais')->references('id')->on('pais');
            $table->string('Ativo');
            $table->string('Cidade');
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('dados_pessoais');
    }
};
