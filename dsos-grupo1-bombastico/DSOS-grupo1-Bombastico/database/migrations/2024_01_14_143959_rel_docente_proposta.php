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

        Schema::table('rel_docente_proposta', function (Blueprint $table) {
                $table->id();
                $table->integer('id_proposta')->unsigned();
                $table->integer('id_docente');
                $table->foreign('id_proposta')->references('id')->on('propostas')->onDelete('cascade');;
                $table->foreign('id_docente')->references('id')->on('docentes')->onDelete('cascade');;
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
