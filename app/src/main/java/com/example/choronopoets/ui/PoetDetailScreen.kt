package com.example.choronopoets.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.choronopoets.components.MarkdownText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.choronopoets.R
import com.example.choronopoets.domain.ai.ChatMode
import com.example.choronopoets.images.poetImages
import com.example.choronopoets.navigation.Screen
import com.example.choronopoets.ui.components.TajikCard
import com.example.choronopoets.ui.components.TajikOutlinedButton
import com.example.choronopoets.ui.components.TajikPrimaryButton
import com.example.choronopoets.viewmodel.PoetDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PoetDetailScreen(
    navController: NavController,
    poetId: Int,
) {
    val viewModel: PoetDetailsViewModel = koinViewModel(parameters = { parametersOf(poetId) })
    val state by viewModel.state.collectAsStateWithLifecycle()
    val poetEnabled = state.poet != null

    // ── Fun Fact dialog ────────────────────────────────────────────────────────
    if (state.funFact != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissFunFact() },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "Интересный факт",
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
                        text = state.funFact!!,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissFunFact() }) {
                    Text("Закрыть", color = MaterialTheme.colorScheme.primary)
                }
            },
        )
    }

    if (state.funFactError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissFunFact() },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Ошибка", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(state.funFactError!!, color = MaterialTheme.colorScheme.onSurface)
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissFunFact() }) {
                    Text("Закрыть", color = MaterialTheme.colorScheme.primary)
                }
            },
        )
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // ── Top bar ──────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = state.poet?.name ?: "Поэт",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            // ── Photo + bio card ─────────────────────────────────────────────
            item {
                TajikCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Image(
                            painter = painterResource(
                                poetImages.getOrElse(poetId) { R.drawable.ic_launcher_foreground }
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                        )
                        if (state.poet?.bio?.isNotBlank() == true) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.poet!!.bio,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                                lineHeight = 20.sp,
                            )
                        }
                    }
                }
            }

            // ── AI actions card ──────────────────────────────────────────────
            item {
                TajikCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Действия ИИ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        TajikPrimaryButton(
                            text = "О поэте",
                            modifier = Modifier.fillMaxWidth(),
                            enabled = poetEnabled,
                            icon = Icons.Default.AutoAwesome,
                        ) {
                            navController.navigate(
                                Screen.POET_CHAT_DB_SCREEN.createRoute(
                                    poetId = poetId,
                                    mode = ChatMode.ASK.id,
                                ),
                            )
                        }

                        TajikOutlinedButton(
                            text = "Говорить с поэтом",
                            modifier = Modifier.fillMaxWidth(),
                            enabled = poetEnabled,
                            icon = Icons.Default.ChatBubble,
                        ) {
                            navController.navigate(
                                Screen.POET_CHAT_DB_SCREEN.createRoute(
                                    poetId = poetId,
                                    mode = ChatMode.ROLEPLAY.id,
                                ),
                            )
                        }

                        TajikOutlinedButton(
                            text = "Написать стихотворение",
                            modifier = Modifier.fillMaxWidth(),
                            enabled = poetEnabled,
                            icon = Icons.Default.Create,
                        ) {
                            navController.navigate(
                                Screen.POET_POEM_GENERATE_SCREEN.createRoute(poetId = poetId),
                            )
                        }

                        TajikOutlinedButton(
                            text = if (state.isFunFactLoading) "Загружаю факт..." else "Интересный факт",
                            modifier = Modifier.fillMaxWidth(),
                            enabled = poetEnabled && !state.isFunFactLoading,
                            icon = Icons.Default.EmojiObjects,
                        ) {
                            viewModel.loadFunFact()
                        }

                        if (state.isFunFactLoading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.dp,
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "ИИ ищет интересный факт...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                )
                            }
                        }
                    }
                }
            }

            // ── Poems section header ─────────────────────────────────────────
            item {
                Text(
                    text = "Стихотворения",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            itemsIndexed(state.poems, key = { _, it -> it.id }) { index, poem ->
                TajikCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${index + 1}.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = poem.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        if (poem.content.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = poem.content,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                lineHeight = 18.sp,
                                maxLines = 6,
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
