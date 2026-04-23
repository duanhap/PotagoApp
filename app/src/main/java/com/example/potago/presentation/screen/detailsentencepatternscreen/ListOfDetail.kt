package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.potago.presentation.navigation.Screen
import com.example.potago.R

data class SentenceItem(val sentence: String, val meaning: String)

@Composable
fun ListOfDetailScreen(
	navController: NavController,
	patternId: Int = 0,
	viewModel: ListOfDetailViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()
	
	var selectedTab by remember { mutableStateOf(0) }
	var openMenuIndex by remember { mutableStateOf(-1) }

	// Load sentences khi screen được tạo
	LaunchedEffect(patternId) {
		if (patternId > 0) {
			viewModel.loadSentences(patternId)
		}
	}

	// Refresh khi quay lại từ AddSentence
	LaunchedEffect(Unit) {
		if (patternId > 0) {
			viewModel.refreshSentences(patternId)
		}
	}

	val sentences = uiState.filteredSentences

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
				// Header: back + Danh sách câu
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.padding(bottom = 18.dp)
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
						"Danh sách câu",
						color = Color(0xFF000000),
						fontSize = 28.sp,
						fontWeight = FontWeight.Bold,
						modifier = Modifier.padding(start = 12.dp)
					)
				}

				// Loading state
				if (uiState.isLoading) {
					Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
						androidx.compose.material3.CircularProgressIndicator(color = Color(0xFF58CC02))
					}
				} else if (uiState.error != null) {
					Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
						Text(text = uiState.error ?: "Lỗi tải dữ liệu", color = Color.Red)
					}
				} else {
					// Tab filter
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.padding(bottom = 21.dp, start = 21.dp)
					) {
						listOf("Tất cả" to "all", "Chưa thuộc" to "unknown", "Đã thuộc" to "known").forEachIndexed { index, (label, status) ->
							val isSelected = uiState.selectedFilter == status
							val hPad = when (label) {
								"Tất cả" -> 24.dp
								"Chưa thuộc" -> 20.dp
								else -> 26.dp
							}
							Column(
								modifier = Modifier
									.padding(end = if (index < 2) 9.dp else 0.dp)
									.border(
										width = 1.dp,
										color = if (isSelected) Color(0xFF000000) else Color(0x1A000000),
										shape = RoundedCornerShape(24.dp)
									)
									.clip(RoundedCornerShape(24.dp))
									.background(Color(0xFFFFFFFF))
									.clickable { viewModel.filterByStatus(status) }
									.padding(vertical = 11.dp, horizontal = hPad)
							) {
								Text(label, color = Color(0xFF000000), fontSize = 12.sp, fontWeight = FontWeight.Bold)
							}
						}
					}

					// Search bar
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier
							.padding(bottom = 27.dp, start = 21.dp, end = 21.dp)
							.border(width = 1.dp, color = Color(0xFFE5E7EB), shape = RoundedCornerShape(30.dp))
							.clip(RoundedCornerShape(30.dp))
							.fillMaxWidth()
							.background(Color(0xFFF9FAFB))
							.padding(vertical = 11.dp)
					) {
						Icon(
							painter = painterResource(id = R.drawable.ic_question),
							contentDescription = "Search",
							tint = Color(0xFFCCCCCC),
							modifier = Modifier.padding(start = 15.dp, end = 10.dp).size(18.dp)
						)
						Text("Nhập câu tìm kiếm", color = Color(0xFFCCCCCC), fontSize = 14.sp, fontWeight = FontWeight.Bold)
					}

					// Danh sách câu
					if (sentences.isEmpty()) {
						Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
							Text("Không có câu nào", color = Color.Gray)
						}
					} else {
						sentences.forEachIndexed { index, sentence ->
							SentenceCard(
								sentence = sentence.term,
								meaning = sentence.definition,
								status = sentence.status,
								isMenuOpen = openMenuIndex == index,
								onMenuToggle = {
									openMenuIndex = if (openMenuIndex == index) -1 else index
								},
								onEdit = { 
									openMenuIndex = -1
									navController.navigate(Screen.EditSentence(sentence.id))
								},
								onDelete = { 
									openMenuIndex = -1
									viewModel.deleteSentence(sentence.id)
								},
								onToggleStatus = {
									openMenuIndex = -1
									val newStatus = if (sentence.status == "known") "unknown" else "known"
									viewModel.updateSentenceStatus(sentence.id, newStatus)
								},
								modifier = Modifier.padding(
									bottom = 20.dp,
									start = 20.dp,
									end = 20.dp
								)
							)
						}
					}

					// Khoảng trống để + button không che nội dung
					Spacer(modifier = Modifier.height(80.dp))
				}
			}
		}

		// Nút + xanh ở giữa đáy
		Box(
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(bottom = 24.dp)
				.size(56.dp)
				.clip(CircleShape)
				.background(Color(0xFF58CC02))
				.clickable { 
					navController.navigate(Screen.AddSentence(patternId))
				},
			contentAlignment = Alignment.Center
		) {
			Text(
				"+",
				color = Color.White,
				fontSize = 32.sp,
				fontWeight = FontWeight.Bold,
				lineHeight = 32.sp
			)
		}
	}
}

@Composable
private fun SentenceCard(
	sentence: String,
	meaning: String,
	status: String,
	isMenuOpen: Boolean,
	onMenuToggle: () -> Unit,
	onEdit: () -> Unit,
	onDelete: () -> Unit,
	onToggleStatus: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.border(width = 2.dp, color = Color(0x26000000), shape = RoundedCornerShape(24.dp))
			.clip(RoundedCornerShape(24.dp))
			.fillMaxWidth()
			.background(Color(0xFFFFFFFF))
			.padding(21.dp)
	) {
		// Hàng câu tiếng Anh + dấu 3 chấm
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				sentence,
				color = Color(0xFF1F2937),
				fontSize = 14.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.weight(1f)
			)
			// Dấu 3 chấm — click mở/đóng menu
			Box(contentAlignment = Alignment.TopEnd) {
				Text(
					"•••",
					color = Color(0xFF9CA3AF),
					fontSize = 12.sp,
					modifier = Modifier
						.padding(start = 8.dp)
						.clickable { onMenuToggle() }
				)
				// Dropdown menu
				if (isMenuOpen) {
					Column(
						modifier = Modifier
							.offset(y = 20.dp)
							.shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
							.clip(RoundedCornerShape(12.dp))
							.background(Color(0xFFFFFFFF))
							.border(width = 1.dp, color = Color(0xFFE5E7EB), shape = RoundedCornerShape(12.dp))
							.padding(horizontal = 16.dp, vertical = 8.dp)
							.width(140.dp)
					) {
						// Tùy chọn thay đổi status (Đã thuộc/Chưa thuộc)
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier
								.padding(vertical = 8.dp)
								.clickable { onToggleStatus() }
						) {
							Icon(
								painter = painterResource(id = R.drawable.ic_check_detailed_video_screen),
								contentDescription = if (status == "unknown") "Mark as known" else "Mark as unknown",
								tint = Color(0xFF58CC02),
								modifier = Modifier.padding(end = 10.dp).size(16.dp)
							)
							Text(
								if (status == "unknown") "Đã thuộc" else "Chưa thuộc",
								color = Color(0xFF4B5563),
								fontSize = 13.sp,
								fontWeight = FontWeight.Bold
							)
						}
						
						// Chỉnh sửa
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier
								.padding(vertical = 8.dp)
								.clickable { onEdit() }
						) {
							Icon(
								painter = painterResource(id = R.drawable.ic_detail_course_screen_edit),
								contentDescription = "Edit",
								tint = Color(0xFF58CC02),
								modifier = Modifier.padding(end = 10.dp).size(16.dp)
							)
							Text("Chỉnh sửa", color = Color(0xFF4B5563), fontSize = 13.sp, fontWeight = FontWeight.Bold)
						}
						
						// Xóa
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier
								.padding(vertical = 8.dp)
								.clickable { onDelete() }
						) {
							Icon(
								painter = painterResource(id = R.drawable.ic_bin),
								contentDescription = "Delete",
								tint = Color(0xFFEF4444),
								modifier = Modifier.padding(end = 10.dp).size(14.dp)
							)
							Text("Xóa", color = Color(0xFF4B5563), fontSize = 13.sp, fontWeight = FontWeight.Bold)
						}
					}
				}
			}
		}

		Spacer(modifier = Modifier.height(8.dp))

		// Hàng nghĩa tiếng Việt + icon loa
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				meaning,
				color = Color(0xFF9CA3AF),
				fontSize = 14.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.weight(1f)
			)
			Image(
				painter = painterResource(id = R.drawable.loa),
				contentDescription = "Potato illustration",
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.padding(start = 20.dp,)
					.size(24.dp)
					.width(37.dp)
					.height(39.dp)
			)
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListOfDetailScreenPreview() {
	ListOfDetailScreen(rememberNavController(), 1)
}
