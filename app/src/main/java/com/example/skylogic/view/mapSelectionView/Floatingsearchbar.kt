package com.example.skylogic.view.mapSelectionView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skylogic.R

@Composable
fun FloatingSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .height(52.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Search icon
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF4DA3FF),
                modifier = Modifier.size(22.dp)
            )

            Spacer(Modifier.width(10.dp))

            // Text input
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(
                    color = Color(0xFF1A1A2E),
                    fontSize = 15.sp
                ),
                cursorBrush = SolidColor(Color(0xFF4DA3FF)),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() }
                ),
                decorationBox = { innerTextField ->
                    // Placeholder
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.SearchCity),
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}