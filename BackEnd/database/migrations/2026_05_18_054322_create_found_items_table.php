<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('found_items', function (Blueprint $table) {

            $table->uuid('id')->primary();

            $table->uuid('user_id');

            $table->foreignId('category_id')
                ->constrained('categories')
                ->onDelete('cascade');

            $table->string('item_name');

            $table->text('description')->nullable();

            $table->string('found_location');

            $table->date('date_found');

            $table->text('image_url')->nullable();

            $table->string('status')->default('belum');

            $table->timestamp('created_at')->useCurrent();

            $table->foreign('user_id')
                ->references('id')
                ->on('users')
                ->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('found_items');
    }
};