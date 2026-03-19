package com.example.choronopoets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommonNationalityCard(
    selected: String?,
    onSelect: (String?) -> Unit
) {
    val options = listOf(
        null to "Все",
        "RU" to "RU",
        "ENG" to "ENG",
        "FR" to "FR",
        "DE" to "DE",
        "TJ" to "TJ"
    )

    Row(
       modifier = Modifier
           .fillMaxWidth()
           .padding(top = 8.dp, start = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        options.forEach { (code, label) ->
            FilterChip(
                selected = selected == code,
                onClick = { onSelect(code) },
                label = { Text(label) }
            )
        }
    }
}