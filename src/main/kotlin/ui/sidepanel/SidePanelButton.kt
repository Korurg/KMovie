package com.github.korurg.ui.sidepanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.github.korurg.ui.Tooltip

@Composable
fun SidePanelButton(
    imageVector: ImageVector,
    contentDescription: String,
    expanded: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(6.dp)
    Tooltip(contentDescription) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .clip(shape)
                .background(if (selected) colors.surface else Color.Transparent)
                .drawBehind {
                    if (selected) {
                        drawRoundRect(
                            color = colors.primary,
                            topLeft = Offset(0f, 6.dp.toPx()),
                            size = Size(3.dp.toPx(), (size.height - 12.dp.toPx()).coerceAtLeast(0f)),
                            cornerRadius = CornerRadius(2.dp.toPx())
                        )
                    }
                }
        ) {
            TextButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (selected) colors.primary else colors.onSurfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = contentDescription
                    )
                    if (expanded) {
                        Spacer(Modifier.width(8.dp))
                        Text(contentDescription, maxLines = 1)
                    }
                }
            }
        }
    }
}
