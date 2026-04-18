package com.example.potago.presentation.screen.detailsetencepatternscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import com.example.potago.R

@Composable
fun EditDetailScreen(navController: NavController) {
	var titleValue by remember { mutableStateOf("") }
	var descValue by remember { mutableStateOf("") }

	Box(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight()
				.background(color = Color(0xFFFFFFFF))
		) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.background(color = Color(0xFFFFFFFF))
				.verticalScroll(rememberScrollState())
		) {
			// Header: nút back + tiêu đề màn hình
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 49.dp)
					.fillMaxWidth()
					.background(color = Color(0xE3FFFFFF))
					.shadow(elevation = 2.dp, spotColor = Color(0x1A000000))
					.padding(vertical = 11.dp)
			) {
				Image(
					painter = painterResource(id = R.drawable.ic_back),
					contentDescription = "Back",
					colorFilter = ColorFilter.tint(Color.Black),
					modifier = Modifier
						.padding(start = 20.dp)
						.size(24.dp)
						.clickable { navController.popBackStack() }
				)
				Text(
					"Chỉnh sửa mẫu câu",
					color = Color(0xFF000000),
					fontSize = 24.sp,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(start = 12.dp)
				)
			}

			// Trường tiêu đề
			BasicTextField(
				value = titleValue,
				onValueChange = { titleValue = it },
				textStyle = TextStyle(
					color = Color(0xFF000000),
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold
				),
				modifier = Modifier
					.padding(bottom = 0.dp, start = 22.dp, end = 22.dp)
					.fillMaxWidth(),
				decorationBox = { innerTextField ->
					Box {
						if (titleValue.isEmpty()) {
							Text(
								"Nhập tiêu đề vô đây",
								color = Color(0x80000000),
								fontSize = 16.sp,
								fontWeight = FontWeight.Bold
							)
						}
						innerTextField()
					}
				}
			)
			Spacer(modifier = Modifier.height(14.dp))
			// Gạch chân tiêu đề
			Box(
				modifier = Modifier
					.padding(bottom = 6.dp, start = 20.dp, end = 20.dp)
					.height(2.dp)
					.fillMaxWidth()
					.background(color = Color(0xFF000000))
			)
			Text(
				"Tiêu đề",
				color = Color(0xFF000000),
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(bottom = 36.dp, start = 20.dp)
			)

			// Trường mô tả
			BasicTextField(
				value = descValue,
				onValueChange = { descValue = it },
				textStyle = TextStyle(
					color = Color(0xFF000000),
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold
				),
				modifier = Modifier
					.padding(start = 22.dp, end = 22.dp)
					.fillMaxWidth(),
				decorationBox = { innerTextField ->
					Box {
						if (descValue.isEmpty()) {
							Text(
								"Nhập mô tả vô đây",
								color = Color(0x80000000),
								fontSize = 16.sp,
								fontWeight = FontWeight.Bold
							)
						}
						innerTextField()
					}
				}
			)
			Spacer(modifier = Modifier.height(13.dp))
			// Gạch chân mô tả
			Box(
				modifier = Modifier
					.padding(bottom = 6.dp, start = 20.dp, end = 20.dp)
					.height(2.dp)
					.fillMaxWidth()
					.background(color = Color(0xFF000000))
			)
			Text(
				"Mô tả",
				color = Color(0xFF000000),
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(bottom = 44.dp, start = 21.dp)
			)

			// Ngôn ngữ của câu
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 4.dp, start = 23.dp, end = 23.dp)
					.fillMaxWidth()
			) {
				Text(
					"English",
					color = Color(0x80000000),
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold
				)
				Image(
					painter = painterResource(id = R.drawable.ic_dropdown),
					contentDescription = "Potato illustration",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.padding(start = 20.dp,)
						.size(24.dp)
						.width(37.dp)
						.height(39.dp)
				)
			}
			Box(
				modifier = Modifier
					.padding(bottom = 7.dp, start = 22.dp, end = 22.dp)
					.height(2.dp)
					.fillMaxWidth()
					.background(color = Color(0xFF000000))
			)
			Text(
				"Ngôn ngữ của câu",
				color = Color(0xFF000000),
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(bottom = 47.dp, start = 22.dp)
			)

			// Ngôn ngữ của nghĩa
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 7.dp, start = 20.dp, end = 20.dp)
					.fillMaxWidth()
			) {
				Text(
					"Tiếng Việt",
					color = Color(0x80000000),
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold
				)
				Image(
					painter = painterResource(id = R.drawable.ic_dropdown),
					contentDescription = "Potato illustration",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.padding(start = 20.dp,)
						.size(24.dp)
						.width(37.dp)
						.height(39.dp)
				)
			}
			Box(
				modifier = Modifier
					.padding(bottom = 5.dp, start = 22.dp, end = 22.dp)
					.height(2.dp)
					.fillMaxWidth()
					.background(color = Color(0xFF000000))
			)
			Text(
				"Ngôn ngữ của nghĩa",
				color = Color(0xFF000000),
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(bottom = 40.dp, start = 22.dp)
			)

				Spacer(modifier = Modifier.height(80.dp))
			}
		}

		// Nút dấu tích xanh ở giữa đáy
		Box(
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(bottom = 24.dp)
				.size(56.dp)
				.clip(CircleShape)
				.background(Color(0xFF58CC02))
				.clickable { navController.popBackStack() },
			contentAlignment = Alignment.Center
		) {
			Image(
				painter = painterResource(id = R.drawable.ic_check_detailed_video_screen),
				contentDescription = "Potato illustration",
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.fillMaxWidth()
			)
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditDetailScreenPreview() {
	EditDetailScreen(rememberNavController())
}
