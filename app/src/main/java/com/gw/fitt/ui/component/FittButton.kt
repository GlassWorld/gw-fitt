package com.gw.fitt.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gw.fitt.ui.theme.FittTheme
import com.gw.fitt.ui.theme.fittColors

private val buttonTextStyle @Composable get() = MaterialTheme.typography.labelMedium.copy(
    fontWeight = FontWeight(600),
    fontSize = 15.sp
)

@Composable
fun FittButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val accent = MaterialTheme.fittColors.accent
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = accent,
            contentColor = Color(0xFF1A1A1A),
            disabledContainerColor = accent.copy(alpha = 0.38f),
            disabledContentColor = Color(0xFF1A1A1A).copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(text = text, style = buttonTextStyle)
    }
}

@Composable
fun FittGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(text = text, style = buttonTextStyle)
    }
}

@Preview(showBackground = true)
@Composable
private fun FittButtonPreview() {
    FittTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FittButton(text = "운동 시작", onClick = {}, modifier = Modifier.fillMaxWidth())
            FittGhostButton(text = "취소", onClick = {}, modifier = Modifier.fillMaxWidth())
            FittButton(text = "비활성화", onClick = {}, modifier = Modifier.fillMaxWidth(), enabled = false)
        }
    }
}
