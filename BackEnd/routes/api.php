<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\CommentController;

Route::prefix('v1')->group(function () {

    // GET semua komentar
    Route::get('/comments', [CommentController::class, 'index']);

    // GET komentar berdasarkan item
    Route::get(
        '/comments/{item_type}/{item_id}',
        [CommentController::class, 'getByItem']
    );

    // GET detail komentar
    Route::get('/comment/{id}', [CommentController::class, 'show']);

    // CREATE komentar
    Route::post('/comment', [CommentController::class, 'store']);

    // UPDATE komentar
    Route::put('/comment/{id}', [CommentController::class, 'update']);

    // DELETE komentar
    Route::delete('/comment/{id}', [CommentController::class, 'destroy']);
});
