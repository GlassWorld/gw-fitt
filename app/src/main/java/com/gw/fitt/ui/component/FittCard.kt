package com.gw.fitt.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gw.fitt.ui.theme.FittTheme

@Composable
fun FittCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
    val border = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            colors = cardColors,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = border
        ) {
            Column(content = content)
        }
    } else {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            colors = cardColors,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = border
        ) {
            Column(content = content)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FittCardPreview() {
    FittTheme {
        FittCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "벤치 프레스", style = MaterialTheme.typography.headlineMedium)
                Text(text = "가슴 / 삼두", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
