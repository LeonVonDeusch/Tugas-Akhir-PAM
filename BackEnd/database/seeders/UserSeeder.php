<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;

class UserSeeder extends Seeder
{
    public function run(): void
    {
        User::create([
            'id' => '550e8400-e29b-41d4-a716-446655440000',
            'email' => 'leon@example.com',
            'full_name' => 'Leon Von Deusch',
            'phone' => '08123456789',
            'faculty' => 'Teknik Informatika',
            'avatar_url' => null
        ]);

        User::create([
            'id' => '550e8400-e29b-41d4-a716-446655440001',
            'email' => 'john@example.com',
            'full_name' => 'John Doe',
            'phone' => '08987654321',
            'faculty' => 'Sistem Informasi',
            'avatar_url' => null
        ]);
    }
}