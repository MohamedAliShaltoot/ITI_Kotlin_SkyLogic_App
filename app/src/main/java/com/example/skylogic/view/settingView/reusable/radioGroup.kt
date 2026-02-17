package com.example.skylogic.view.settingView.reusable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RadioGroup(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {

    Column {
        options.forEach { option ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF4DA3FF),
                        unselectedColor = Color.White.copy(alpha = 0.6f)
                    )
                )


                Spacer(Modifier.width(8.dp))
                Text(
                    option,
                    color = if (option == selected)
                        Color(0xFF4DA3FF)
                    else
                        Color.White
                )

            }
        }
    }
}