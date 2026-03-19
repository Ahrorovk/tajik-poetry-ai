package com.example.choronopoets.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choronopoets.R

@Composable
fun CommonPoemsListCard(
    title: String,
    text: String,
    onClick: () -> Unit
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ){
        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Poems Info",
            modifier = Modifier
                .size(60.dp)
                .clip(shape = RoundedCornerShape(6.dp)),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ){
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = text,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 3.dp)
            )
        }
    }
}

//@Preview(showSystemUi = true)
//@Composable
//fun CommonPoetsCardPreview(){
//    CommonPoemsListCard()
//}

//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(100.dp)
//            .padding(8.dp)
//            .padding(top = 4.dp)
//            .shadow(elevation = 4.dp)
//            .clickable { onClick() },
//        colors = CardDefaults.cardColors(
//            containerColor = if (onColorChange) Color.White else Color(0xFF1E1E1E),
//            contentColor = if (onColorChange) Color.Black else Color(0xFFFFFFFF)
//        )
//    ){
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(6.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ){
//            Text(
//                text = title,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.SemiBold
//            )
//        }
//    }