package com.example.choronopoets.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.choronopoets.R
import com.example.choronopoets.domain.repositories.TajikPoet
import com.example.choronopoets.images.centuryImages
import com.example.choronopoets.navigation.Screen
import com.example.choronopoets.ui.components.TajikCard
import com.example.choronopoets.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .systemBarsPadding(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // ── Hero banner ──────────────────────────────────────────────────
            item {
                HeroBanner(
                    onSettingsClick = { navController.navigate(Screen.SETTINGS_SCREEN.route) },
                )
            }

            // ── Эпохи ────────────────────────────────────────────────────────
            item {
                SectionHeader(
                    title = "Эпохи",
                    subtitle = "Путешествие по векам поэзии",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.centuries, key = { it.id }) { century ->
                        CenturyCard(
                            centuryName = century.century,
                            imageResId = centuryImages[century.century] ?: R.drawable.ic_launcher_background,
                            onClick = {
                                navController.navigate(Screen.POETS_SCREEN.createRoute(centuryId = century.id))
                            },
                        )
                    }
                }
            }

            // ── Таджикские поэты ─────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Таджикские поэты",
                    subtitle = "Легенды персидской литературы",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.tajikPoets, key = { it.key.id }) { poet ->
                        TajikPoetCard(
                            poet = poet,
                            onClick = {
                                navController.navigate(Screen.TAJIK_POET_DETAIL_SCREEN.createRoute(poet.key.id))
                            },
                        )
                    }
                }
            }

            // ── Генератор стихов ─────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Генератор стихов",
                    subtitle = "Создай своё стихотворение с ИИ",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                GeneratePoemBanner(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { navController.navigate(Screen.POEM_GENERATOR_SCREEN.route) },
                )
            }

            // ── Избранное ─────────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Избранное",
                    subtitle = "Сохранённые стихотворения",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                FavoritesBanner(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { navController.navigate(Screen.FAVORITES_SCREEN.route) },
                )
            }

            // ── История чатов ─────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "История чатов",
                    subtitle = "Все диалоги с поэтами",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                ChatHistoryBanner(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { navController.navigate(Screen.CHAT_HISTORY_SCREEN.route) },
                )
            }
        }
    }
}

// ── Composables ───────────────────────────────────────────────────────────────

@Composable
private fun HeroBanner(onSettingsClick: () -> Unit) {
    val surface = MaterialTheme.colorScheme.surface
    val background = MaterialTheme.colorScheme.background
    val onBackground = MaterialTheme.colorScheme.onBackground
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(surface, background),
                ),
            )
            .padding(horizontal = 20.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(primary, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "✦", color = Color.White, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Поэзия ИИ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = onBackground,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Открывай мир поэзии вместе с искусственным интеллектом",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = onBackground.copy(alpha = 0.7f),
                lineHeight = 18.sp,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatChip(label = "Поэты")
                StatChip(label = "Чат с ИИ")
                StatChip(label = "Стихи")
            }
        }
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.align(Alignment.TopEnd),
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Настройки",
                tint = onBackground.copy(alpha = 0.8f),
            )
        }
    }
}

@Composable
private fun FavoritesBanner(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF7F1D1D), Color(0xFFB91C1C)),
                ),
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Мои избранные стихи",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = "Нажми ❤ на стихотворении, чтобы сохранить",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun StatChip(label: String) {
    val primary = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .background(primary.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text = label, color = primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = 20.dp, bottom = 10.dp)) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
        )
    }
}

@Composable
private fun CenturyCard(
    centuryName: String,
    imageResId: Int,
    onClick: () -> Unit,
) {
    TajikCard(
        modifier = Modifier
            .width(160.dp)
            .height(210.dp)
            .clickable { onClick() },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(imageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                        ),
                    )
                    .padding(12.dp),
            ) {
                Text(
                    text = centuryName,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun TajikPoetCard(
    poet: TajikPoet,
    onClick: () -> Unit,
) {
    TajikCard(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(poet.imageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = poet.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = poet.shortDescription,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(text = "Открыть →", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ChatHistoryBanner(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ),
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Мои диалоги с поэтами",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "История всех чатов с ИИ-поэтами",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Composable
private fun GeneratePoemBanner(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.primary,
                    ),
                ),
            )
            .clickable { onClick() }
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Создай стихотворение",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Выбери тему и стиль — ИИ напишет за тебя",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 16.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
