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

        Schema::create('rel_projetos_propostas', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('id_proposta')->unsigned();
            $table->foreign('id_proposta')->references('id')->on('propostas');
            $table->integer('id_projeto')->unsigned();
            $table->foreign('id_projeto')->references('id')->on('projetos');
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        //
    }
};
