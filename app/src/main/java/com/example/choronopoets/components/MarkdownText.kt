package com.example.choronopoets.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    val textColor = if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onBackground
    val bodyStyle = MaterialTheme.typography.bodyMedium.let {
        if (fontSize != TextUnit.Unspecified) it.copy(fontSize = fontSize) else it
    }
    val codeStyle = MaterialTheme.typography.bodySmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
    )

    Markdown(
        content = text,
        modifier = modifier,
        colors = DefaultMarkdownColors(
            text = textColor,
            codeText = textColor,
            inlineCodeText = textColor,
            linkText = MaterialTheme.colorScheme.primary,
            codeBackground = MaterialTheme.colorScheme.surface,
            inlineCodeBackground = MaterialTheme.colorScheme.surface,
            dividerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
            tableText = textColor,
            tableBackground = MaterialTheme.colorScheme.surface,
        ),
        typography = DefaultMarkdownTypography(
            h1 = MaterialTheme.typography.headlineLarge.copy(color = textColor),
            h2 = MaterialTheme.typography.headlineMedium.copy(color = textColor),
            h3 = MaterialTheme.typography.headlineSmall.copy(color = textColor),
            h4 = MaterialTheme.typography.titleLarge.copy(color = textColor),
            h5 = MaterialTheme.typography.titleMedium.copy(color = textColor),
            h6 = MaterialTheme.typography.titleSmall.copy(color = textColor),
            text = bodyStyle.copy(color = textColor),
            code = codeStyle.copy(color = textColor),
            inlineCode = codeStyle.copy(color = textColor),
            quote = bodyStyle.copy(color = textColor.copy(alpha = 0.7f)),
            paragraph = bodyStyle.copy(color = textColor),
            ordered = bodyStyle.copy(color = textColor),
            bullet = bodyStyle.copy(color = textColor),
            list = bodyStyle.copy(color = textColor),
            link = bodyStyle.copy(color = MaterialTheme.colorScheme.primary),
        ),
    )
}
