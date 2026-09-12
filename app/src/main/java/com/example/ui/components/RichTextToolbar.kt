package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

import com.example.util.LocalAppStrings

@Composable
fun RichTextToolbar(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Heading
            ToolbarButton(
                icon = Icons.Default.FormatSize,
                contentDescription = strings.heading,
                testTag = "toolbar_heading",
                onClick = {
                    insertAtLineStart(textFieldValue, onValueChange, "# ")
                }
            )

            // Bold
            ToolbarButton(
                icon = Icons.Default.FormatBold,
                contentDescription = strings.bold,
                testTag = "toolbar_bold",
                onClick = {
                    wrapSelection(textFieldValue, onValueChange, "**", "**")
                }
            )

            // Italic
            ToolbarButton(
                icon = Icons.Default.FormatItalic,
                contentDescription = strings.italic,
                testTag = "toolbar_italic",
                onClick = {
                    wrapSelection(textFieldValue, onValueChange, "*", "*")
                }
            )

            // Strikethrough
            ToolbarButton(
                icon = Icons.Default.FormatStrikethrough,
                contentDescription = strings.strikethrough,
                testTag = "toolbar_strikethrough",
                onClick = {
                    wrapSelection(textFieldValue, onValueChange, "~~", "~~")
                }
            )

            // Bullet List
            ToolbarButton(
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                contentDescription = strings.bulletList,
                testTag = "toolbar_bullet_list",
                onClick = {
                    insertAtLineStart(textFieldValue, onValueChange, "• ")
                }
            )

            // Numbered List
            ToolbarButton(
                icon = Icons.Default.Numbers,
                contentDescription = strings.numberedList,
                testTag = "toolbar_numbered_list",
                onClick = {
                    insertAtLineStart(textFieldValue, onValueChange, "1. ")
                }
            )

            // Quote
            ToolbarButton(
                icon = Icons.Default.FormatQuote,
                contentDescription = strings.quote,
                testTag = "toolbar_quote",
                onClick = {
                    insertAtLineStart(textFieldValue, onValueChange, "> ")
                }
            )
        }
    }
}

@Composable
private fun ToolbarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    testTag: String,
    onClick: () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(38.dp)
            .testTag(testTag),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun wrapSelection(
    current: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    prefix: String,
    suffix: String
) {
    val text = current.text
    val start = current.selection.min
    val end = current.selection.max

    if (start != end) {
        val selected = text.substring(start, end)
        val newText = text.substring(0, start) + prefix + selected + suffix + text.substring(end)
        val newSelection = TextRange(start + prefix.length, end + prefix.length)
        onValueChange(TextFieldValue(newText, newSelection))
    } else {
        // No selection: insert wrapper and place cursor inside
        val newText = text.substring(0, start) + prefix + suffix + text.substring(start)
        val newSelection = TextRange(start + prefix.length)
        onValueChange(TextFieldValue(newText, newSelection))
    }
}

private fun insertAtLineStart(
    current: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    insertText: String
) {
    val text = current.text
    val cursor = current.selection.start
    // Find start of current line
    val lineStart = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let {
        if (it == -1) 0 else it + 1
    }
    val newText = text.substring(0, lineStart) + insertText + text.substring(lineStart)
    val newSelection = TextRange(cursor + insertText.length)
    onValueChange(TextFieldValue(newText, newSelection))
}
