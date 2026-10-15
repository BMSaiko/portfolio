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

        Schema::create('propostas', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('id_empresa')->unsigned();
            $table->foreign('id_empresa')->references('id')->on('empresa');
            $table->string('Titulo');
            $table->text('Descricao');
            $table->string('Tipo de Trabalho');
            $table->string('localização');
            $table->integer('vagas');
            $table->timestamp('DataPublicacao');
            $table->integer('id_estado')->unsigned();
            $table->foreign('id_estado')->references('id')->on('estado_proposta');
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('propostas');
    }
};
