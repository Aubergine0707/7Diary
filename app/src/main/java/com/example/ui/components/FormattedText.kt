package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormattedText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val lines = text.split("\n")

    Column(modifier = modifier.fillMaxWidth()) {
        lines.forEachIndexed { index, rawLine ->
            val trimmed = rawLine.trim()
            when {
                trimmed.startsWith("### ") -> {
                    Text(
                        text = parseInlineFormatting(trimmed.removePrefix("### ")),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                trimmed.startsWith("## ") -> {
                    Text(
                        text = parseInlineFormatting(trimmed.removePrefix("## ")),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
                trimmed.startsWith("# ") -> {
                    Text(
                        text = parseInlineFormatting(trimmed.removePrefix("# ")),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
                trimmed.startsWith("> ") -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(22.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = parseInlineFormatting(trimmed.removePrefix("> ")),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                trimmed.startsWith("• ") || trimmed.startsWith("- ") -> {
                    val bulletContent = if (trimmed.startsWith("• ")) trimmed.removePrefix("• ") else trimmed.removePrefix("- ")
                    Row(modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)) {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = parseInlineFormatting(bulletContent),
                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                        )
                    }
                }
                trimmed.matches(Regex("^\\d+\\.\\s+.*")) -> {
                    val number = trimmed.substringBefore(".").trim()
                    val numContent = trimmed.substringAfter(". ").trim()
                    Row(modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)) {
                        Text(
                            text = "$number. ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                        Text(
                            text = parseInlineFormatting(numContent),
                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                        )
                    }
                }
                rawLine.isEmpty() -> {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                else -> {
                    Text(
                        text = parseInlineFormatting(rawLine),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textColor,
                            lineHeight = 22.sp
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Parses markdown inline formatting:
 * **bold** -> FontWeight.Bold
 * *italic* -> FontStyle.Italic
 * ~~strikethrough~~ -> TextDecoration.LineThrough
 */
fun parseInlineFormatting(input: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        while (cursor < input.length) {
            // Check for bold **text**
            if (input.startsWith("**", cursor)) {
                val end = input.indexOf("**", cursor + 2)
                if (end != -1) {
                    val boldText = input.substring(cursor + 2, end)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(boldText)
                    pop()
                    cursor = end + 2
                    continue
                }
            }

            // Check for strikethrough ~~text~~
            if (input.startsWith("~~", cursor)) {
                val end = input.indexOf("~~", cursor + 2)
                if (end != -1) {
                    val strikeText = input.substring(cursor + 2, end)
                    pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                    append(strikeText)
                    pop()
                    cursor = end + 2
                    continue
                }
            }

            // Check for italic *text*
            if (input[cursor] == '*' && cursor + 1 < input.length && input[cursor + 1] != '*') {
                val end = input.indexOf('*', cursor + 1)
                if (end != -1) {
                    val italicText = input.substring(cursor + 1, end)
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(italicText)
                    pop()
                    cursor = end + 1
                    continue
                }
            }

            append(input[cursor])
            cursor++
        }
    }
}
