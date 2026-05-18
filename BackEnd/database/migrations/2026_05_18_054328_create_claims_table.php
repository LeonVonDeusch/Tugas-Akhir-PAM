<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('claims', function (Blueprint $table) {

            $table->uuid('id')->primary();

            $table->uuid('found_item_id');

            $table->uuid('claimer_id');

            $table->text('proof_description');

            $table->string('contact_info');

            $table->text('message')->nullable();

            $table->string('status')->default('pending');

            $table->timestamp('created_at')->useCurrent();

            $table->foreign('found_item_id')
                ->references('id')
                ->on('found_items')
                ->onDelete('cascade');

            $table->foreign('claimer_id')
                ->references('id')
                ->on('users')
                ->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('claims');
    }
};