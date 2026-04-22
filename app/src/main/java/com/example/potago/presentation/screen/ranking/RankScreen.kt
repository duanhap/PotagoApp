package com.example.potago.presentation.screen.ranking
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.potago.R

@Composable
fun RankScreen() {
	val textField1 = remember { mutableStateOf("") }
	val textField2 = remember { mutableStateOf("") }
	val textField3 = remember { mutableStateOf("") }
	val textField4 = remember { mutableStateOf("") }
	val context = LocalContext.current

	fun getDrawableId(vararg names: String): Int {
		for (name in names) {
			val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
			if (resId != 0) return resId
		}
		return 0
	}

	val rank1Icon = getDrawableId("top1", "1", "_1", "rank_1")
	val rank2Icon = getDrawableId("top2", "2", "_2", "rank_2")
	val rank3Icon = getDrawableId("top3", "3", "_3", "rank_3")
	val yellowHairAvatar = getDrawableId("avataryellowhair", "avatar_yellow_hair")
	val top1000Banner = getDrawableId("top1000", "Top 1000", "top_1000")
	val pinkgirl = getDrawableId("pinkgirl", "pink_girl")
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
				.padding(bottom = 17.dp,)
				.verticalScroll(rememberScrollState())
		){

			Column(
				modifier = Modifier
					.padding(bottom = 25.dp,)
					.fillMaxWidth()
			){
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.fillMaxWidth()
						.background(
							color = Color(0xFFFFFFFF),
						)
						.shadow(
							elevation = 2.dp,
							spotColor = Color(0x1A000000),
						)
						.padding(vertical = 4.dp,)
				){
					Image(
						painter = painterResource(id = R.drawable.ic_cancel),
						contentDescription = "Potato illustration",
						contentScale = ContentScale.Crop,
						colorFilter = ColorFilter.tint(Color.Black),
						modifier = Modifier
							.padding(start = 20.dp,)
							.size(24.dp)
							.width(37.dp)
							.height(39.dp)
					)
					Text("Xếp hạng",
						color = Color(0xFF000000),
						fontSize = 32.sp,
						fontWeight = FontWeight.Bold,
						modifier = Modifier
							.padding(start = 20.dp,)
					)
				}

				Column(
					modifier = Modifier
						.clip(shape = RoundedCornerShape(bottomEnd = 20.dp,))
						.fillMaxWidth()

						.padding(top = 10.dp,start = 14.dp,end = 26.dp,)
				){
					Image(
						painter = painterResource(id = R.drawable.top1000),
						contentDescription = "Potato illustration",
						modifier = Modifier
							.fillMaxWidth()
					)
				}
			}
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(bottom = 14.dp,start = 20.dp,end = 20.dp,)
					.border(
						width = 1.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.padding(vertical = 9.dp,horizontal = 13.dp,)
			){
				Image(
					painter = painterResource(id = R.drawable.top1),
					contentDescription = "Potato illustration",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.padding(end = 20.dp,)
						.width(37.dp)
						.height(39.dp)
				)
				Image(
					painter = painterResource(id = R.drawable.pinkgirl),
					contentDescription = "Potato illustration",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.padding(end = 20.dp,)
						.width(37.dp)
						.height(39.dp)
				)
				Column(
				){
					Text("雯雯",
						color = Color(0xFF000000),
						fontSize = 16.sp,
						fontWeight = FontWeight.Bold,
					)
				}
				Column(
					modifier = Modifier
						.weight(1f)
				){
				}
				Column(
				){
					Text("345890102450 xp",
						color = Color(0xFF000000),
						fontSize = 14.sp,
						fontWeight = FontWeight.Bold,
					)
				}
			}
			Column(
				modifier = Modifier
					.padding(bottom = 15.dp,start = 19.dp,end = 19.dp,)
					.border(
						width = 1.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.padding(top = 10.dp,bottom = 10.dp,start = 14.dp,)
			){
				Image(
					painter = painterResource(id = R.drawable.top2),
					contentDescription = "Potato illustration",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.padding(end = 20.dp,)
						.width(37.dp)
						.height(39.dp)
				)
			}
			Column(
				modifier = Modifier
					.padding(bottom = 15.dp,start = 19.dp,end = 19.dp,)
					.border(
						width = 1.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.padding(top = 10.dp,bottom = 10.dp,start = 14.dp,)
			){
				Image(
					painter = painterResource(id = R.drawable.top3),
					contentDescription = "Potato illustration",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.padding(end = 20.dp,)
						.width(37.dp)
						.height(39.dp)
				)
			}
			TextFieldView(
				placeholder = "4",
				value = textField1.value,
				onValueChange = { newText -> textField1.value = newText },
				textStyle = TextStyle(
					color = Color(0xFF050505),
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold,
				),
				modifier = Modifier
					.padding(bottom = 15.dp,start = 19.dp,end = 19.dp,)
					.border(
						width = 1.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.padding(vertical = 23.dp,horizontal = 28.dp,)
			)
			TextFieldView(
				placeholder = "5",
				value = textField2.value,
				onValueChange = { newText -> textField2.value = newText },
				textStyle = TextStyle(
					color = Color(0xFF050505),
					fontSize = 18.sp,
					fontWeight = FontWeight.Bold,
				),
				modifier = Modifier
					.padding(bottom = 13.dp,start = 19.dp,end = 19.dp,)
					.border(
						width = 1.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.padding(vertical = 23.dp,horizontal = 29.dp,)
			)
			TextFieldView(
				placeholder = "6",
				value = textField3.value,
				onValueChange = { newText -> textField3.value = newText },
				textStyle = TextStyle(
					color = Color(0xFF050505),
					fontSize = 18.sp,
					fontWeight = FontWeight.Bold,
				),
				modifier = Modifier
					.padding(bottom = 14.dp,start = 19.dp,end = 19.dp,)
					.border(
						width = 1.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.padding(vertical = 22.dp,horizontal = 28.dp,)
			)
			TextFieldView(
				placeholder = "7",
				value = textField4.value,
				onValueChange = { newText -> textField4.value = newText },
				textStyle = TextStyle(
					color = Color(0xFF050505),
					fontSize = 18.sp,
					fontWeight = FontWeight.Bold,
				),
				modifier = Modifier
					.padding(bottom = 13.dp,start = 19.dp,end = 19.dp,)
					.border(
						width = 1.dp,
						color = Color(0x1A000000),
						shape = RoundedCornerShape(15.dp)
					)
					.clip(shape = RoundedCornerShape(15.dp))
					.fillMaxWidth()
					.background(
						color = Color(0xFFFFFFFF),
						shape = RoundedCornerShape(15.dp)
					)
					.padding(vertical = 23.dp,horizontal = 28.dp,)
			)
			Column(
				modifier = Modifier
					.padding(horizontal = 9.dp,)
					.fillMaxWidth()
			){
				Column(
					modifier = Modifier
						.padding(horizontal = 10.dp,)
						.border(
							width = 1.dp,
							color = Color(0x1A000000),
							shape = RoundedCornerShape(15.dp)
						)
						.clip(shape = RoundedCornerShape(15.dp))
						.height(59.dp)
						.fillMaxWidth()
						.background(
							color = Color(0xFFFFFFFF),
							shape = RoundedCornerShape(15.dp)
						)
				){
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.border(
							width = 1.dp,
							color = Color(0xFF89E219),
							shape = RoundedCornerShape(15.dp)
						)
						.clip(shape = RoundedCornerShape(15.dp))
						.fillMaxWidth()
						.background(
							color = Color(0xFFC4FF7A),
							shape = RoundedCornerShape(15.dp)
						)
						.padding(16.dp)
				){
					Text("2222",
						color = Color(0xFF050505),
						fontSize = 18.sp,
						fontWeight = FontWeight.Bold,
						modifier = Modifier
							.padding(end = 22.dp,)
					)
					AsyncImage(
						model = if (yellowHairAvatar != 0) yellowHairAvatar else "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/jCkVt59ei4/htkcd7ho_expires_30_days.png",
						contentDescription = null,
						contentScale = ContentScale.Crop,
						modifier = Modifier
							.padding(end = 20.dp,)
							.clip(shape = RoundedCornerShape(9999.dp))
							.width(41.dp)
							.height(41.dp)
					)
					Column(
					){
						Text("Dunsensei",
							color = Color(0xFF000000),
							fontSize = 16.sp,
							fontWeight = FontWeight.Bold,
						)
					}
					Column(
						modifier = Modifier
							.weight(1f)
					){
					}
					Column(
					){
						Text("2450 xp",
							color = Color(0xFF000000),
							fontSize = 14.sp,
							fontWeight = FontWeight.Bold,
						)
					}
				}
			}
		}
	}
}

@Composable
fun TextFieldView(
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
fun RankScreenPreview() {
	RankScreen()
}