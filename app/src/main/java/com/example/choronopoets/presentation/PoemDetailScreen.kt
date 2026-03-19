package com.example.choronopoets.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LightbulbCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.choronopoets.components.MarkdownText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.choronopoets.ui.components.TajikCard
import com.example.choronopoets.ui.components.TajikOutlinedButton
import com.example.choronopoets.ui.components.TajikPrimaryButton
import com.example.choronopoets.utils.TtsManager
import com.example.choronopoets.viewmodel.PoemDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PoemDetailScreen(
    navController: NavController,
    poemId: Int,
) {
    val viewModel: PoemDetailViewModel = koinViewModel(parameters = { parametersOf(poemId) })
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val ttsManager = remember { TtsManager(context) }
    val isSpeaking by ttsManager.isSpeaking.collectAsStateWithLifecycle()
    val ttsReady by ttsManager.isReady.collectAsStateWithLifecycle()
    val ttsError by ttsManager.error.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose { ttsManager.shutdown() }
    }

    // ── Explain dialog ─────────────────────────────────────────────────────────
    if (state.explainText != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissExplain() },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Объяснение стихотворения",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    MarkdownText(
                        text = state.explainText!!,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissExplain() }) {
                    Text("Закрыть", color = MaterialTheme.colorScheme.primary)
                }
            },
        )
    }

    // ── Explain error dialog ───────────────────────────────────────────────────
    if (state.explainError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissExplain() },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Ошибка", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    text = state.explainError!!,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissExplain() }) {
                    Text("Закрыть", color = MaterialTheme.colorScheme.primary)
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
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = state.poem?.title ?: "Стихотворение",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                    )
                }
                // Heart icon
                IconButton(onClick = { viewModel.toggleFavorite() }) {
                    Icon(
                        imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (state.isFavorite) "Удалить из избранного" else "Добавить в избранное",
                        tint = if (state.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            if (state.poem == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // ── Title card ────────────────────────────────────────────
                    TajikCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp),
                            )
                            Text(
                                text = state.poem!!.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    // ── Poem content ──────────────────────────────────────────
                    TajikCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = state.poem!!.content,
                            modifier = Modifier.padding(20.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Start,
                        )
                    }

                    // ── Action buttons ────────────────────────────────────────
                    TajikCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            // Explain button
                            TajikPrimaryButton(
                                text = if (state.isExplaining) "Анализирую..." else "Объяснить стихотворение",
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isExplaining,
                                icon = Icons.Default.LightbulbCircle,
                                onClick = { viewModel.explainPoem() },
                            )

                            // TTS button
                            TajikOutlinedButton(
                                text = when {
                                    isSpeaking -> "Остановить"
                                    !ttsReady -> "Инициализация..."
                                    else -> "Слушать"
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = ttsReady || isSpeaking,
                                icon = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                onClick = {
                                    if (isSpeaking) {
                                        ttsManager.stop()
                                    } else {
                                        ttsManager.speak(
                                            text = state.poem!!.content,
                                            title = state.poem!!.title,
                                        )
                                    }
                                },
                            )

                            // TTS error hint
                            if (ttsError != null) {
                                Text(
                                    text = "⚠ $ttsError",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }

                    // ── Explain loading indicator ─────────────────────────────
                    AnimatedVisibility(
                        visible = state.isExplaining,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp,
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "ИИ анализирует стихотворение...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
