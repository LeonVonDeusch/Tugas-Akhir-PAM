<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('comments', function (Blueprint $table) {

            $table->uuid('id')->primary();

            $table->uuid('user_id');

            $table->uuid('item_id');

            $table->string('item_type');

            $table->text('content');

            $table->timestamp('created_at')->useCurrent();

            $table->timestamp('updated_at')->nullable();

            $table->foreign('user_id')
                ->references('id')
                ->on('users')
                ->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('comments');
    }
};