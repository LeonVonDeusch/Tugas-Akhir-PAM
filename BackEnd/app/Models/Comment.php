<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Concerns\HasUuids;

class Comment extends Model
{
    use HasUuids;

    public $incrementing = false;

    protected $keyType = 'string';

    public $timestamps = false;

    protected $fillable = [
        'user_id',
        'item_id',
        'item_type',
        'content',
        'parent_id' // Optional, untuk komentar balasan
    ];

    public function parent()
    {
        return $this->belongsTo(Comment::class, 'parent_id');
    }
}
