package com.example.tugasakhirpam.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tugasakhirpam.model.Comment
import com.example.tugasakhirpam.viewmodel.CommentUiState
import com.example.tugasakhirpam.viewmodel.CommentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    itemType: String,
    itemId: String,
    userId: String,
    onBackClick: () -> Unit,
    commentViewModel: CommentViewModel = viewModel()
) {
    val uiState by commentViewModel.uiState.collectAsStateWithLifecycle()
    val replyTarget by commentViewModel.replyTarget.collectAsStateWithLifecycle()
    var commentText by remember { mutableStateOf("") }

    // Inisialisasi ViewModel saat screen pertama dibuka
    LaunchedEffect(itemType, itemId) {
        commentViewModel.init(itemType, itemId)
    }

    // Tampilkan snackbar untuk ActionSuccess/Error
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CommentUiState.ActionSuccess -> {
                snackbarHostState.showSnackbar(state.message)
                commentViewModel.resetState()
            }
            is CommentUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                commentViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Komentar / Diskusi") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // Banner reply (muncul jika sedang reply ke komentar tertentu)
                if (replyTarget != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Membalas: ${replyTarget?.content}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            TextButton(onClick = { commentViewModel.clearReplyTarget() }) {
                                Text("Batal")
                            }
                        }
                    }
                }

                // Input komentar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Tulis komentar...") },
                        modifier = Modifier.weight(1f),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            commentViewModel.createComment(userId, commentText)
                            commentText = ""
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Kirim")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            when (val state = uiState) {
                is CommentUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CommentUiState.Success -> {
                    if (state.comments.isEmpty()) {
                        Text(
                            "Belum ada komentar.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.comments) { (comment, level) ->
                                CommentItem(
                                    comment = comment,
                                    level = level,
                                    currentUserId = userId,
                                    onReply = { commentViewModel.setReplyTarget(it) },
                                    onDelete = { comment.id?.let { id -> commentViewModel.deleteComment(id) } },
                                    onEdit = { /* Bisa tambahkan dialog edit di sini */ }
                                )
                            }
                        }
                    }
                }
                is CommentUiState.Error -> {
                    Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    level: Int,
    currentUserId: String,
    onReply: (Comment) -> Unit,
    onDelete: (Comment) -> Unit,
    onEdit: (Comment) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = (level * 16).dp)) {
        // Indikator thread (garis vertikal untuk reply)
        if (level > 0) {
            Spacer(modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .padding(end = 8.dp))
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = comment.user_email ?: "Anonim",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = comment.content ?: "")
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onReply(comment) }) { Text("Balas") }
                    if (comment.user_id == currentUserId) {
                        TextButton(onClick = { onEdit(comment) }) { Text("Edit") }
                        TextButton(onClick = { onDelete(comment) }) { Text("Hapus") }
                    }
                }
            }
        }
    }
}