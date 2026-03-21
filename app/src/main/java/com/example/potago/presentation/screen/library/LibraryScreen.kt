package com.example.potago.presentation.screen.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.potago.R

@Composable
fun LibraryScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.COURSE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        LibraryHeader()
        LibraryTabSection(
            selectedTab = selectedTab,
            onSelectTab = { selectedTab = it }
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EmptyContentState(
                message = if (selectedTab == LibraryTab.COURSE) {
                    "Chưa có học phần nào cả"
                } else {
                    "Chưa có mẫu câu nào cả"
                }
            )
        }
    }
}

private enum class LibraryTab {
    COURSE,
    SENTENCE
}

@Composable
private fun LibraryHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thư viện",
                    fontSize = 32.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add",
                    modifier = Modifier.size(36.dp),
                    tint = Color.Unspecified
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFF3F4F6)
            )
        }
    }
}

@Composable
private fun LibraryTabSection(
    selectedTab: LibraryTab,
    onSelectTab: (LibraryTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LibraryTabChip(
            text = "Học phần",
            selected = selectedTab == LibraryTab.COURSE,
            onClick = { onSelectTab(LibraryTab.COURSE) }
        )
        LibraryTabChip(
            text = "Mẫu câu",
            selected = selectedTab == LibraryTab.SENTENCE,
            onClick = { onSelectTab(LibraryTab.SENTENCE) }
        )
    }
}

@Composable
private fun LibraryTabChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(103.dp)
            .height(34.dp)
            .background(Color.White, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = if (selected) Color.Black else Color(0x1A000000),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
    }
}

@Composable
private fun EmptyContentState(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_library_sleeping_potago),
            contentDescription = "Sleeping potato",
            modifier = Modifier
                .size(120.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = message,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0x80000000)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LibraryScreenPreview() {
    LibraryScreen(navController = NavController(LocalContext.current))
}
