package com.example.choronopoets.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommonCenturyListCard(
    imageResId: Int,
    text: String,
    onClick: () -> Unit,
//    onColorChange: Boolean
){
    Box(
        modifier = Modifier
            .width(265.dp)
            .height(450.dp)
            .clip(shape = RoundedCornerShape(6.dp))
            .shadow(elevation = 4.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(imageResId),
            contentDescription = "Centuries Images",
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = text,
            fontSize = 36.sp,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = text,
            fontSize = 32.sp,
            textAlign = TextAlign.Start
        )
    }
}



//Card(
//modifier = Modifier
//.fillMaxWidth()
//.height(110.dp)
//.padding(8.dp)
//.padding(top = 6.dp)
//.shadow(elevation = 4.dp)
//.clickable { onClick() },
//colors = CardDefaults.cardColors(
//containerColor = if (onColorChange) Color.White else Color(0xFF1E1E1E),
//contentColor = if (onColorChange) Color.Black else Color(0xFFFFFFFF)
//)
//){
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ){
//        Image(
//            painter = painterResource(imageResId),
//            contentDescription = text,
//            modifier = Modifier
//                .padding(10.dp)
//                .size(65.dp)
//                .clip(CircleShape),
//            contentScale = ContentScale.Crop
//        )
//        Text(
//            text = text,
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}