package com.example.choronopoets.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.choronopoets.R
import com.example.choronopoets.components.CommonInfoPage1
import com.example.choronopoets.components.CommonPoemsListCard
import com.example.choronopoets.components.CommonPoetDetailScreenCards
import com.example.choronopoets.components.CommonTopBar
import com.example.choronopoets.images.poetImages
import com.example.choronopoets.navigation.Screen
import com.example.choronopoets.viewmodel.PoetryUIEvent
import com.example.choronopoets.viewmodel.PoetryViewModel
//navController: NavController, viewModel: PoetryViewModel, poetId: Int
@Composable
fun PoetDetailsScreen() {
//    val state by viewModel.state.collectAsState()
//
//    LaunchedEffect(poetId) {
//        viewModel.processEvent(PoetryUIEvent.LoadPoetDetails(poetId))
//        viewModel.processEvent(PoetryUIEvent.LoadPoems(poetId))
//    }


    Box (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(0.23f)
            .background(Color(0xFFC4C4C4))
    )
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.back_icon),
        contentDescription = "PopBackStack",
        Modifier
            .padding(top = 70.dp)
            .padding(start = 20.dp)
            .size(24.dp),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Draft",
            Modifier
                .padding(top = 110.dp)
                .size(150.dp)
                .clip(shape = CircleShape)
                .border(width = 1.dp, Color.Black, shape = CircleShape),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Alexandr Sergeyevich Pushkin",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        CommonPoetDetailScreenCards(
            onClick1 = {  },
            onClick2 = {  }
        )
    }

    Text(
        text = "О поэте",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(top = 384.dp)
            .padding(start = 24.dp)
    )
    Text(
//        Here will be text from DB
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Viverra rutrum elementum nunc velit dui dui, penatibus.",
        fontSize = 12.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(top = 412.dp)
            .padding(start = 24.dp)
    )
    Spacer(modifier = Modifier.height(34.dp))

    Box(
        modifier = Modifier
            .padding(top = 470.dp)
            .fillMaxWidth()
            .height(600.dp)
            .background(Color(0xFFF4F4F4), shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp)
        ) {
            Text(
                text = "Произведения поэта",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp)
            )
            Spacer(Modifier.height(30.dp))

            CommonPoemsListCard(
                title = "Произведение № ",
                text = "Lorem ipsum dolor sit amet, consect...",
                onClick = {  }
            )
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun CommonPoetsLIstCardPreview(){
    PoetDetailsScreen()
}


//    Scaffold(
//        topBar = {
//            CommonTopBar(
//                title = state.selectedPoet?.name ?: "Неизвестный поэт",
//                onBackClick = { navController.popBackStack() },
//                onToggleTheme = {
//                    viewModel.processEvent(PoetryUIEvent.ToggleTheme)
//                },
//                isDarkMode = state.isDarkMode,
//                titleFontSize = 32
//            )
//        },
//        containerColor = if (state.isDarkMode) Color(0xFF121212) else Color(0xFFF5F5F5)
//    ) { innerPadding ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(start = 4.dp, end = 4.dp)
//        ) {
//            item {
//                state.selectedPoet?.let {
//                    CommonInfoPage1(
//                        imageResId = poetImages[it.id] ?: R.drawable.ic_launcher_background,
//                        bio = it.bio,
//                        isDarkMode = state.isDarkMode
//                    )
//                }
//            }
//
//            items(state.poems) { poem ->
//                CommonPoemsListCard(
//                    title = poem.title,
//                    onClick = {
//                        navController.navigate(Screen.POEM_DETAIL_SCREEN.createRoute(poemId = poem.id))
//                    },
//                    onColorChange = !state.isDarkMode
//                )
//            }
//        }
//    }