<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Comment;
use Illuminate\Http\Request;

class CommentController extends Controller
{
    /**
     * GET ALL COMMENTS
     */
    public function index()
    {
        $comments = Comment::latest('created_at')->get();

        return response()->json([
            'success' => true,
            'message' => 'List semua komentar',
            'data' => $comments
        ]);
    }

    /**
     * GET COMMENTS BY ITEM
     */
    public function getByItem($item_type, $item_id)
    {
        $comments = Comment::with('parent')
        ->where('item_type', $item_type)
        ->where('item_id', $item_id)
        ->latest('created_at')
        ->get();

        return response()->json([
            'success' => true,
            'message' => 'Komentar berdasarkan item',
            'data' => $comments
        ]);
    }

    /**
     * STORE COMMENT
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'user_id' => 'required|uuid',
            'item_id' => 'required|uuid',
            'item_type' => 'required|in:lost,found',
            'content' => 'required|string',
            'parent_id' => 'nullable|uuid|exists:comments,id' // Optional, untuk komentar balasan
        ]);

        $comments = Comment::with('parent')->get();
        
        $comment = Comment::create([
            'user_id' => $validated['user_id'],
            'item_id' => $validated['item_id'],
            'item_type' => $validated['item_type'],
            'content' => $validated['content'],
            'parent_id' => $validated['parent_id'] ?? null // Optional, untuk komentar balasan
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Komentar berhasil dibuat',
            'data' => $comment
        ], 201);
    }

    /**
     * SHOW DETAIL COMMENT
     */
    public function show($id)
    {
        $comment = Comment::find($id);

        if (!$comment) {
            return response()->json([
                'success' => false,
                'message' => 'Komentar tidak ditemukan'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'data' => $comment
        ]);
    }

    /**
     * UPDATE COMMENT
     */
    public function update(Request $request, $id)
    {
        $comment = Comment::find($id);

        if (!$comment) {
            return response()->json([
                'success' => false,
                'message' => 'Komentar tidak ditemukan'
            ], 404);
        }

        $validated = $request->validate([
            'content' => 'required|string'
        ]);

        $comment->update([
            'content' => $validated['content'],
            'updated_at' => now()
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Komentar berhasil diupdate',
            'data' => $comment
        ]);
    }

    /**
     * DELETE COMMENT
     */
    public function destroy($id)
    {
        $comment = Comment::find($id);

        if (!$comment) {
            return response()->json([
                'success' => false,
                'message' => 'Komentar tidak ditemukan'
            ], 404);
        }

        $comment->delete();

        return response()->json([
            'success' => true,
            'message' => 'Komentar berhasil dihapus'
        ]);
    }
}