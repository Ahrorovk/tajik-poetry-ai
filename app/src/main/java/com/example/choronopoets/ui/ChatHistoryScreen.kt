package com.example.choronopoets.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.choronopoets.domain.ai.AiRequestType
import com.example.choronopoets.navigation.Screen
import com.example.choronopoets.ui.components.TajikCard
import com.example.choronopoets.viewmodel.ChatHistoryViewModel
import com.example.choronopoets.viewmodel.ChatSessionItem
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val SECTION_ORDER = listOf(
    AiRequestType.POET_CHAT_ROLEPLAY,
    AiRequestType.POET_CHAT_ASK,
    AiRequestType.POEM_GENERATION,
    AiRequestType.POET_POEM,
    AiRequestType.FUN_FACT,
    AiRequestType.POEM_EXPLANATION,
    AiRequestType.UNKNOWN,
)

@Composable
fun ChatHistoryScreen(navController: NavController) {
    val viewModel: ChatHistoryViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.sessionToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDelete() },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text("Удалить?", fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                Text("История этого запроса будет удалена безвозвратно.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), fontSize = 14.sp)
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDelete() }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDelete() }) {
                    Text("Отмена", color = MaterialTheme.colorScheme.primary)
                }
            },
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .systemBarsPadding(),
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад",
                        tint = MaterialTheme.colorScheme.onBackground)
                }
                Icon(Icons.Default.Forum, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = "История запросов",
                    fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                )
                if (state.sessions.isNotEmpty()) {
                    IconButton(onClick = { viewModel.deleteAll() }) {
                        Icon(Icons.Default.Delete, "Удалить всё",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                    }
                }
            }

            // ── Body ──────────────────────────────────────────────────────────
            if (!state.isLoading && state.sessions.isEmpty()) {
                EmptyHistoryPlaceholder()
            } else {
                val grouped = state.sessions.groupBy { it.type }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SECTION_ORDER.forEach { type ->
                        val items = grouped[type] ?: return@forEach

                        // ── Section header ────────────────────────────────────
                        item(key = "header_${type.name}") {
                            SectionHeader(type = type)
                        }

                        items(items, key = { it.sessionKey }) { session ->
                            SessionCard(
                                session = session,
                                onClick = { navController.navigate(buildNavRoute(session)) },
                                onDelete = { viewModel.requestDelete(session.sessionKey) },
                            )
                        }

                        item(key = "spacer_${type.name}") {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ── Route builder ─────────────────────────────────────────────────────────────

private fun buildNavRoute(session: ChatSessionItem): String = when (session.type) {
    AiRequestType.POET_CHAT_ROLEPLAY,
    AiRequestType.POET_CHAT_ASK -> when (session.source) {
        "db"    -> Screen.POET_CHAT_DB_SCREEN.createRoute(
            session.poetIdOrKey.toIntOrNull() ?: 0, session.modeId)
        "tajik" -> Screen.POET_CHAT_TAJIK_SCREEN.createRoute(session.poetIdOrKey, session.modeId)
        else    -> Screen.AI_SESSION_VIEWER_SCREEN.createRoute(session.sessionKey, session.poetName)
    }
    else -> Screen.AI_SESSION_VIEWER_SCREEN.createRoute(session.sessionKey, session.poetName)
}

// ── Composables ───────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(type: AiRequestType) {
    val color = Color(type.sectionColor)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = AiRequestType.icon(type),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = type.label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 0.8.sp,
        )
    }
}

@Composable
private fun SessionCard(
    session: ChatSessionItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val typeColor = Color(session.type.sectionColor)

    TajikCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Icon ──────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = AiRequestType.icon(session.type),
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(20.dp),
                )
            }

            // ── Content ───────────────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.poetName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = session.lastMessage,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = formatTimestamp(session.lastTimestamp),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    )
                    Text("·", fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Text(
                        text = "${session.messageCount} сообщ.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    )
                    Text("·", fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Text(
                        text = "Открыть →",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = typeColor.copy(alpha = 0.8f),
                    )
                }
            }

            // ── Delete ────────────────────────────────────────────────────────
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.DeleteOutline, "Удалить",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(17.dp),
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Default.Forum, null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
            Text("История запросов пуста", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
            Text(
                text = "Все запросы к ИИ будут сохраняться здесь:\nчаты с поэтами, генерация стихов,\nобъяснения и интересные факты",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000       -> "только что"
        diff < 3_600_000    -> "${diff / 60_000} мин. назад"
        diff < 86_400_000   -> "${diff / 3_600_000} ч. назад"
        diff < 604_800_000  -> "${diff / 86_400_000} дн. назад"
        else -> SimpleDateFormat("d MMM yyyy", Locale("ru")).format(Date(timestamp))
    }
}
