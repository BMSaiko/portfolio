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

        Schema::create('empresa', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('id_dados')->unsigned();
            $table->foreign('id_dados')->references('id')->on('dados_pessoais');
            $table->integer('id_acesso')->unsigned();
            $table->foreign('id_acesso')->references('id')->on('acesso');
            $table->string('diretor');
            $table->string('image');
            $table->string('aprovada');
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('empresa');
    }
};
