package com.example.tugasakhirpam.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LaptopChromebook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Watch
import androidx.compose.ui.graphics.vector.ImageVector

internal fun String?.asCategoryIcon(): ImageVector {
    return when (this) {
        "backpack" -> Icons.Filled.Backpack
        "book" -> Icons.Filled.Book
        "devices" -> Icons.Filled.Devices
        "wallet" -> Icons.Filled.CreditCard
        "key" -> Icons.Filled.Key
        "badge" -> Icons.Filled.Badge
        "watch" -> Icons.Filled.Watch
        "headphones" -> Icons.Filled.Headphones
        "laptop" -> Icons.Filled.LaptopChromebook
        "more" -> Icons.Filled.MoreHoriz
        else -> Icons.Filled.Category
    }
}
