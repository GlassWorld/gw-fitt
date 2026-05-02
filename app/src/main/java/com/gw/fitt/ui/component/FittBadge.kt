package com.gw.fitt.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gw.fitt.ui.theme.FittTheme

enum class Difficulty(val label: String) {
    BEGINNER("초급"),
    INTERMEDIATE("중급"),
    ADVANCED("고급")
}

private data class BadgeColors(val container: Color, val content: Color)

private fun Difficulty.colors(isDark: Boolean): BadgeColors = when (this) {
    Difficulty.BEGINNER -> if (isDark)
        BadgeColors(Color(0xFF1B3A2A), Color(0xFF6FCF97))
    else
        BadgeColors(Color(0xFFDCF5E8), Color(0xFF1A7A3A))

    Difficulty.INTERMEDIATE -> if (isDark)
        BadgeColors(Color(0xFF2A3A1A), Color(0xFFE8FF5A))
    else
        BadgeColors(Color(0xFFF5F5DC), Color(0xFF7A7A00))

    Difficulty.ADVANCED -> if (isDark)
        BadgeColors(Color(0xFF3A1A1A), Color(0xFFEF9A9A))
    else
        BadgeColors(Color(0xFFFFE8E8), Color(0xFFB71C1C))
}

@Composable
fun FittBadge(
    difficulty: Difficulty,
    modifier: Modifier = Modifier
) {
    val colors = difficulty.colors(isSystemInDarkTheme())

    Text(
        text = difficulty.label,
        style = MaterialTheme.typography.labelMedium,
        color = colors.content,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(colors.container)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun FittBadgePreview() {
    FittTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FittBadge(difficulty = Difficulty.BEGINNER)
            FittBadge(difficulty = Difficulty.INTERMEDIATE)
            FittBadge(difficulty = Difficulty.ADVANCED)
        }
    }
}
