package com.example.potago.presentation.screen.detailsetencepatternscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.potago.R

@Composable
fun DeleteDetailScreen(navController: NavController) {
	Box(
		modifier = Modifier
			.fillMaxSize()
	) {
		// Lớp nền mờ (dimmed background)
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color(0x66000000))
				.clickable { navController.popBackStack() }
		)

		// Bottom sheet
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.clip(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
				.background(
					color = Color(0xFFFFFFFF),
					shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
				)
				.padding(top = 12.dp)
				.align(Alignment.BottomCenter)
		) {
			// Drag handle
			Box(
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
					.padding(bottom = 28.dp)
					.clip(shape = RoundedCornerShape(9999.dp))
					.width(47.dp)
					.height(5.dp)
					.background(
						color = Color(0xFFE5E7EB),
						shape = RoundedCornerShape(9999.dp)
					)
			)

			// Mascot + speech bubble
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 32.dp, start = 24.dp)
			) {
				// Mascot image
				Image(
					painter = painterResource(id = R.drawable.mascot_xoacau),
					contentDescription = "Luyện viết",
					contentScale = ContentScale.Fit,
					modifier = Modifier
//						.fillMaxWidth()
						.height(76.dp)
				)

				// Speech bubble "Xác nhận xóa chứ!?"
				Column(
					modifier = Modifier
						.border(
							width = 1.dp,
							color = Color(0xFFE5E7EB),
							shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
						)
						.clip(shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
						.background(
							color = Color(0xFFFFFFFF),
							shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
						)
						.padding(horizontal = 20.dp, vertical = 16.dp)
				) {
					Text(
						"Xác nhận xóa chứ!?",
						color = Color(0xFF000000),
						fontSize = 14.sp,
						fontWeight = FontWeight.Bold
					)
				}
			}

			// Buttons: Từ chối + Xác nhận
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 34.dp, start = 21.dp, end = 21.dp)
					.fillMaxWidth()
			) {
				// Từ chối
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					modifier = Modifier
						.padding(end = 12.dp)
						.clip(shape = RoundedCornerShape(16.dp))
						.weight(1f)
						.background(
							color = Color(0xFFFFFFFF),
							shape = RoundedCornerShape(16.dp)
						)
						.border(
							width = 1.dp,
							color = Color(0xFFE5E7EB),
							shape = RoundedCornerShape(16.dp)
						)
						.clickable { navController.popBackStack() }
						.padding(vertical = 18.dp)
				) {
					Text(
						"Từ chối",
						color = Color(0xFF374151),
						fontSize = 14.sp,
						fontWeight = FontWeight.Bold
					)
				}

				// Xác nhận
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					modifier = Modifier
						.clip(shape = RoundedCornerShape(16.dp))
						.weight(1f)
						.background(
							color = Color(0xFF58CC02),
							shape = RoundedCornerShape(16.dp)
						)
						.clickable {
							// TODO: thực hiện xóa mẫu câu rồi navigate về Library
							navController.popBackStack()
						}
						.padding(vertical = 18.dp)
				) {
					Text(
						"Xác nhận",
						color = Color(0xFFFFFFFF),
						fontSize = 14.sp,
						fontWeight = FontWeight.Bold
					)
				}
			}
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DeleteDetailScreenPreview() {
	DeleteDetailScreen(rememberNavController())
}
