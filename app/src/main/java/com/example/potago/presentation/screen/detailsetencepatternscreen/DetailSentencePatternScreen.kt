package com.example.potago.presentation.screen.detailsetencepatternscreen

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen

@Composable
fun DetailSentencePatternScreen(navController: NavController) {
	val textField1 = remember { mutableStateOf("") }
	val textField2 = remember { mutableStateOf("") }
	val textField3 = remember { mutableStateOf("") }
	val textField4 = remember { mutableStateOf("") }
	val textField5 = remember { mutableStateOf("") }
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight()
			.background(
				color = Color(0xFFFFFFFF),
			)
	){
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.background(
					color = Color(0xFFFFFFFF),
				)
				.padding(bottom = 17.dp)
				.verticalScroll(rememberScrollState())
		){
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 22.dp)
					.fillMaxWidth()
					.background(
						color = Color(0xE3FFFFFF),
					)
					.shadow(
						elevation = 2.dp,
						spotColor = Color(0x1A000000),
					)
					.padding(vertical = 11.dp)
			){
				Image(
					painter = painterResource(id = R.drawable.ic_back),
					contentDescription = "Potato illustration",
					contentScale = ContentScale.Crop,
					colorFilter = ColorFilter.tint(Color.Black),
					modifier = Modifier
						.padding(start = 20.dp,)
						.size(28.dp)
						.width(37.dp)
						.height(39.dp)
				)
				Text("Mẫu câu",
					color = Color(0xFF000000),
					fontSize = 32.sp,
					fontWeight = FontWeight.Bold,
					modifier = Modifier
						.padding(start = 20.dp,)
				)
			}
			Text("English",
				color = Color(0xFF000000),
				fontSize = 24.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier
					.padding(bottom = 9.dp, start = 22.dp)
			)
			Column(
				modifier = Modifier
					.padding(bottom = 35.dp, start = 21.dp)
			){
				Text("200 mẫu câu - Tháng 1 năm 2026",
					color = Color(0xFF000000),
					fontSize = 12.sp,
					fontWeight = FontWeight.Bold,
				)
			}
			Text("Learn how to order tacos and ask for the bill.",
				color = Color(0xFF000000),
				fontSize = 14.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier
					.padding(bottom = 44.dp, start = 22.dp)
			)
			Column(
				modifier = Modifier
					.padding(bottom = 12.dp, start = 21.dp)
			){
				Text("Chế độ học",
					color = Color(0xFF000000),
					fontSize = 18.sp,
					fontWeight = FontWeight.Bold,
				)
			}
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 14.dp, start = 19.dp, end = 19.dp)
					.border(
						width = 2.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
			){
				Image(
					painter = painterResource(id = R.drawable.luyenviet),
					contentDescription = "Luyện viết",
					contentScale = ContentScale.Fit,
					modifier = Modifier
						.fillMaxWidth()
						.height(76.dp)
				)

			}
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 27.dp, start = 20.dp, end = 20.dp)
					.border(
						width = 2.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
			){
				Image(
					painter = painterResource(id = R.drawable.sapxepchu),
					contentDescription = "Luyện viết",
					contentScale = ContentScale.Fit,
					modifier = Modifier
						.fillMaxWidth()
						.height(76.dp)
				)

			}
			Column(
				modifier = Modifier
					.padding(bottom = 15.dp, start = 21.dp)
			){
				Text("Tính năng khác",
					color = Color(0xFF000000),
					fontSize = 18.sp,
					fontWeight = FontWeight.Bold,
				)
			}
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 16.dp, start = 21.dp, end = 21.dp)
					.border(
						width = 2.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.clickable { navController.navigate(Screen.ListOfDetail.route) }
			){
				Image(
					painter = painterResource(id = R.drawable.danhsachcau),
					contentDescription = "Xem danh sách câu",
					contentScale = ContentScale.Fit,
					modifier = Modifier
						.fillMaxWidth()
						.height(76.dp)
				)
			}
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 14.dp, start = 21.dp, end = 21.dp)
					.border(
						width = 2.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.clickable { navController.navigate(Screen.EditDetail.route) }
			){
				Image(
					painter = painterResource(id = R.drawable.suacau),
					contentDescription = "Chỉnh sửa mẫu câu",
					contentScale = ContentScale.Fit,
					modifier = Modifier
						.fillMaxWidth()
						.height(76.dp)
				)
			}
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(horizontal = 20.dp)
					.border(
						width = 2.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.clickable { navController.navigate(Screen.DeleteDetail.route) }
			){
				Image(
					painter = painterResource(id = R.drawable.xoacau),
					contentDescription = "Luyện viết",
					contentScale = ContentScale.Fit,
					modifier = Modifier
						.fillMaxWidth()
						.height(76.dp)
				)
			}
		}
	}
}

@Composable
fun DetailSentenceTextFieldView(
	modifier: Modifier = Modifier,
	value: String,
	onValueChange: (String) -> Unit,
	placeholder: String = "",
	textStyle: TextStyle = TextStyle.Default
) {
	BasicTextField(
		value = value,
		onValueChange = onValueChange,
		textStyle = textStyle,
		modifier = modifier,
		decorationBox = { innerTextField ->
			Box {
				if (value.isEmpty()) {
					Text(
						text = placeholder,
						style = textStyle.copy(color = Color(0xB3050505))
					)
				}
				innerTextField()
			}
		}
	)
}

@Preview(showBackground = true)
@Composable
fun DetailSentencePatternScreenPreview() {
	DetailSentencePatternScreen(rememberNavController())
}

