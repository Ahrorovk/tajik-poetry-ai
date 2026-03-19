@file:Suppress("UNUSED_EXPRESSION")

package com.example.choronopoets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.choronopoets.R

@Composable
fun CommonTopBar(
    onBackClick: (() -> Unit)? = null,
    onToggleTheme: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: (() -> Unit)? = null,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White,
                    modifier = Modifier
                        .size(34.dp)
                        .padding(end = 8.dp)
                )
            }
        }

        IconButton(
            onClick = { onSearchClick() }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.search_icon),
                contentDescription = "Filter Icon",
                modifier = Modifier.size(36.dp)
            )
        }

        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier.padding(end = 20.dp)
        ) {
            Icon(
                imageVector = if (!isDarkMode) ImageVector.vectorResource(R.drawable.dark_mode) else ImageVector.vectorResource(R.drawable.light_mode),
                contentDescription = "Toggle Theme button",
                modifier = Modifier.size(30.dp)
            )
        }

        if (onProfileClick != null) {
            IconButton(
                onClick = { onProfileClick },
                modifier = Modifier.padding(end = 20.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.menu_icon),
                    contentDescription = "Icon Menu",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

