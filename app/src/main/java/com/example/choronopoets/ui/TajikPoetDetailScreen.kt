package com.example.choronopoets.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.choronopoets.domain.ai.ChatMode
import com.example.choronopoets.navigation.Screen
import com.example.choronopoets.ui.components.TajikCard
import com.example.choronopoets.ui.components.TajikOutlinedButton
import com.example.choronopoets.ui.components.TajikPrimaryButton
import com.example.choronopoets.viewmodel.TajikPoetDetailViewModel
import com.example.choronopoets.viewmodel.TajikPoemsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TajikPoetDetailScreen(
    navController: NavController,
    poetKey: String,
) {
    val viewModel: TajikPoetDetailViewModel = koinViewModel(parameters = { parametersOf(poetKey) })
    val state by viewModel.state.collectAsStateWithLifecycle()
    val poet = state.poet ?: return

    val poemsViewModel: TajikPoemsViewModel = koinViewModel(parameters = { parametersOf(poet.roomPoetId) })
    val poems by poemsViewModel.poems.collectAsStateWithLifecycle()

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
                        text = poet.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            // ── Photo + short description ────────────────────────────────────
            item {
                TajikCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                    ) {
                        Image(
                            painter = painterResource(poet.imageResId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp)),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = poet.shortDescription,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                            lineHeight = 18.sp,
                        )
                    }
                }
            }

            // ── Bio card ─────────────────────────────────────────────────────
            item {
                TajikCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "О поэте",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = poet.bio,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                            lineHeight = 20.sp,
                        )
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
                            text = "Спросить ИИ об этом поэте",
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.AutoAwesome,
                        ) {
                            navController.navigate(
                                Screen.POET_CHAT_TAJIK_SCREEN.createRoute(
                                    poetKey = poet.key.id,
                                    mode = ChatMode.ASK.id,
                                ),
                            )
                        }

                        TajikOutlinedButton(
                            text = "Поговорить с поэтом",
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.ChatBubble,
                        ) {
                            navController.navigate(
                                Screen.POET_CHAT_TAJIK_SCREEN.createRoute(
                                    poetKey = poet.key.id,
                                    mode = ChatMode.ROLEPLAY.id,
                                ),
                            )
                        }

                        TajikOutlinedButton(
                            text = if (state.isFunFactLoading) "Загружаю факт..." else "Интересный факт",
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isFunFactLoading,
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

            // ── Poems section ─────────────────────────────────────────────────
            if (poems.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Стихотворения",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }

                itemsIndexed(poems, key = { _, it -> it.id }) { index, poem ->
                    TajikCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable {
                                navController.navigate(Screen.POEM_DETAIL_SCREEN.createRoute(poem.id))
                            },
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
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    lineHeight = 18.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Читать полностью →",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
