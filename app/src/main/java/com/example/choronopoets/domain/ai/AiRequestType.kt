package com.example.choronopoets.domain.ai

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.graphics.vector.ImageVector

enum class AiRequestType(
    val label: String,
    val sectionColor: Long,
) {
    POET_CHAT_ROLEPLAY("Чат с поэтом",      0xFFEF4444),
    POET_CHAT_ASK("О поэте",                0xFF3B82F6),
    POEM_GENERATION("Генерация стиха",      0xFF8B5CF6),
    POET_POEM("Стих в стиле поэта",         0xFF10B981),
    FUN_FACT("Интересный факт",             0xFFF59E0B),
    POEM_EXPLANATION("Объяснение стиха",    0xFF06B6D4),
    UNKNOWN("Прочее",                       0xFF6B7280);

    companion object {
        fun fromSessionKey(key: String): AiRequestType = when {
            key.startsWith("poemgen_")  -> POEM_GENERATION
            key.startsWith("poetpoem_") -> POET_POEM
            key.startsWith("funfact_")  -> FUN_FACT
            key.startsWith("explain_")  -> POEM_EXPLANATION
            key.startsWith("db_") || key.startsWith("tajik_") -> {
                val mode = key.split("_", limit = 3).getOrNull(2) ?: "ask"
                if (ChatMode.fromId(mode) == ChatMode.ROLEPLAY) POET_CHAT_ROLEPLAY
                else POET_CHAT_ASK
            }
            else -> UNKNOWN
        }

        fun icon(type: AiRequestType): ImageVector = when (type) {
            POET_CHAT_ROLEPLAY  -> Icons.Default.Psychology
            POET_CHAT_ASK       -> Icons.Default.Forum
            POEM_GENERATION     -> Icons.Default.Create
            POET_POEM           -> Icons.Default.AutoAwesome
            FUN_FACT            -> Icons.Default.Lightbulb
            POEM_EXPLANATION    -> Icons.Default.MenuBook
            UNKNOWN             -> Icons.Default.Forum
        }
    }
}
