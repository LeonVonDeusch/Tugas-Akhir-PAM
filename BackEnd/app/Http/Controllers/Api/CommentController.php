<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\Comment;

class CommentController extends Controller
{
    /**
     * GET COMMENTS
     */
    public function index($itemId)
    {
        $comments = Comment::where('item_id', $itemId)
            ->get();

        return response()->json($comments);
    }

    /**
     * CREATE COMMENT
     */
    public function store(Request $request)
    {
        $request->validate([
            'user_id' => 'required|exists:users,id',
            'item_id' => 'required',
            'item_type' => 'required|in:lost,found',
            'content' => 'required|string'
        ]); 

        $comment = Comment::create($request->all());

        return response()->json($comment, 201);
    }
}