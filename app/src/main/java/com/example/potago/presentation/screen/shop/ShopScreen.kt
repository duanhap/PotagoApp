package com.example.potago.presentation.screen.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.potago.domain.model.Item
import com.example.potago.presentation.screen.UiState
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.potago.R
import com.example.potago.presentation.ui.theme.Nunito
import com.example.potago.presentation.ui.theme.PotagoTheme

// Chỉnh ảnh thủ công: mỗi chỗ một drawable riêng (không dùng chung trong composable).
private val shopHeroLeftRes = R.drawable.ic_twemoji_umbrella_on_ground
private val shopHeroRightRes = R.drawable.ic_icon_park_outline_sun

private enum class OwnedItemKind {
    WaterFreeze,
    SieuKn,
    HackKn
}

private data class OwnedItem(
    val name: String,
    val quantity: Int,
    val imageRes: Int,
    val kind: OwnedItemKind
)

private data class ShopItem(
    val name: String,
    val imageRes: Int,
    val price: Int,
    val oldPrice: Int? = null,
    val itemType: String = "",
    val quantity: Int = 1
)

private sealed class ShopActiveSheet {
    data class OwnedDetail(val item: OwnedItem) : ShopActiveSheet()
    data class PurchaseConfirm(val item: ShopItem) : ShopActiveSheet()
    data class PurchaseInsufficient(val item: ShopItem) : ShopActiveSheet()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    navController: NavController,
    viewModel: ShopViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeSheet by remember { mutableStateOf<ShopActiveSheet?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ShopEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    val item = (uiState.items as? UiState.Success)?.data
    val ownedItems = listOf(
        OwnedItem("Water Freeze", item?.waterStreak ?: 0, R.drawable.noto_ice, OwnedItemKind.WaterFreeze),
        OwnedItem("Siêu KN", item?.superExperience ?: 0, R.drawable.bag_kn, OwnedItemKind.SieuKn),
        OwnedItem("Hack KN", item?.hackExperience ?: 0, R.drawable.bag_hack, OwnedItemKind.HackKn)
    )
    val streakItems = listOf(
        ShopItem("Water Freeze", R.drawable.noto_ice, 200, itemType = ItemType.WATER_STREAK, quantity = 1)
    )
    val superXpItems = listOf(
        ShopItem("Single", R.drawable.bag_kn, 500, itemType = ItemType.SUPER_XP, quantity = 1),
        ShopItem("3 pack", R.drawable.bag_3pack, 1200, 1500, itemType = ItemType.SUPER_XP, quantity = 3),
        ShopItem("5 pack", R.drawable.bag_5pack, 2000, 2500, itemType = ItemType.SUPER_XP, quantity = 5)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
        ShopTopBar(
            diamondCount = uiState.diamond,
            onClose = { navController.popBackStack() }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = shopHeroLeftRes),
                contentDescription = null,
                modifier = Modifier
                    .height(170.dp)
                    .weight(1f),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(shopHeroRightRes),
                contentDescription = null,
                modifier = Modifier.size(108.dp),
                contentScale = ContentScale.Fit
            )
        }

        SectionTitle("Vật phẩm của tôi")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ownedItems.forEach { item ->
                OwnedItemCard(
                    modifier = Modifier.weight(1f),
                    item = item,
                    onClick = { activeSheet = ShopActiveSheet.OwnedDetail(item) }
                )
            }
        }

        val hackKnItems = listOf(
            ShopItem("Hack KN", R.drawable.bag_hack, 300, itemType = ItemType.HACK_XP, quantity = 1)
        )
        val onShopItemClick: (ShopItem) -> Unit = { shopItem ->
            activeSheet = if (uiState.diamond >= shopItem.price) {
                ShopActiveSheet.PurchaseConfirm(shopItem)
            } else {
                ShopActiveSheet.PurchaseInsufficient(shopItem)
            }
        }

        ShopSection("Bảo vệ chuỗi", streakItems, uiState.diamond, onShopItemClick)
        ShopSection("Thời gian siêu cấp", superXpItems, uiState.diamond, onShopItemClick)
        ShopSection("Hack KN", hackKnItems, uiState.diamond, onShopItemClick)
        Spacer(modifier = Modifier.height(24.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        activeSheet?.let { sheet ->
            ModalBottomSheet(
                onDismissRequest = { activeSheet = null },
                sheetState = sheetState,
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                when (sheet) {
                    is ShopActiveSheet.OwnedDetail -> OwnedItemBottomSheetContent(
                        item = sheet.item,
                        onDismiss = { activeSheet = null },
                        onConfirm = { activeSheet = null },
                        onUse = {
                            val itemType = when (sheet.item.kind) {
                                OwnedItemKind.WaterFreeze -> ItemType.WATER_STREAK
                                OwnedItemKind.SieuKn -> ItemType.SUPER_XP
                                OwnedItemKind.HackKn -> ItemType.HACK_XP
                            }
                            viewModel.useItem(itemType)
                            activeSheet = null
                        }
                    )
                    is ShopActiveSheet.PurchaseConfirm -> PurchaseConfirmBottomSheetContent(
                        item = sheet.item,
                        onConfirm = {
                            viewModel.purchase(sheet.item.itemType, sheet.item.quantity)
                            activeSheet = null
                        }
                    )
                    is ShopActiveSheet.PurchaseInsufficient -> PurchaseInsufficientBottomSheetContent(
                        onDismiss = { activeSheet = null }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShopSection(
    title: String,
    items: List<ShopItem>,
    diamond: Int,
    onItemClick: (ShopItem) -> Unit
) {
    Spacer(modifier = Modifier.height(22.dp))
    SectionTitle(title)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            ShopItemRow(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun ShopTopBar(
    diamondCount: Int,
    onClose: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(45f)
                        .clickable(onClick = onClose)
                )

                Text(
                    text = "Cửa hàng",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .height(34.dp)
                        .background(
                            color = Color(0xFFFFE3E0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, Color(0x40F44336), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "♦", color = Color(0xFFF44336))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = diamondCount.toString(),
                        color = Color(0xFFF44336),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFE5E7EB))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = Color.Black,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
private fun OwnedItemBottomSheetContent(
    item: OwnedItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onUse: () -> Unit
) {
    val description = when (item.kind) {
        OwnedItemKind.WaterFreeze -> buildWaterFreezeDescription(item.quantity)
        OwnedItemKind.HackKn -> buildHackKnDescription(item.quantity)
        OwnedItemKind.SieuKn -> buildSieuKnDescription(item.quantity)
    }
    val heroSize = when (item.kind) {
        OwnedItemKind.WaterFreeze -> 112.dp
        OwnedItemKind.HackKn -> 132.dp
        OwnedItemKind.SieuKn -> 124.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 168.dp, height = 44.dp)
                    .offset(y = 52.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x14000000))
            )
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.size(heroSize),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = description,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(28.dp))
        when (item.kind) {
            OwnedItemKind.WaterFreeze -> {
                ShopSheetPrimaryButton(text = "XÁC NHẬN", enabled = true, onClick = onConfirm)
            }
            OwnedItemKind.HackKn, OwnedItemKind.SieuKn -> {
                ShopSheetPrimaryButton(
                    text = "SỬ DỤNG",
                    enabled = item.quantity > 0,
                    onClick = onUse
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ĐỂ SAU",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3B82F6)
                    )
                }
            }
        }
    }
}

/** Xác nhận mua bằng kim cương (Figma 59:1481). */
@Composable
private fun PurchaseConfirmBottomSheetContent(
    item: ShopItem,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier.width(132.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_thinking_mascot_home_screen),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 118.dp, height = 128.dp)
                        .offset(x = (-4).dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "?",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 52.sp),
                    color = Color(0xFF4B4B4B),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 8.dp)
                )
            }
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, start = 2.dp),
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "Xác nhận mua chứ !?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B5563),
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        ShopPurchaseItemPreviewCard(item = item)
        Spacer(modifier = Modifier.height(22.dp))
        ShopSheetPrimaryButton(text = "XÁC NHẬN", enabled = true, onClick = onConfirm)
    }
}

/** Không đủ kim cương (Figma 59:1918). */
@Composable
private fun PurchaseInsufficientBottomSheetContent(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(R.drawable.ic_looking_mascot),
                contentDescription = null,
                modifier = Modifier
                    .width(118.dp)
                    .height(138.dp)
                    .padding(start = 4.dp),
                contentScale = ContentScale.Fit
            )
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp, start = 6.dp),
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "Cần học chăm lên đó chủ nhân :3",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B5563),
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 14.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        Text(
            text = "Số dư không đủ để hoàn thành lượt mua lần này.",
            style = MaterialTheme.typography.titleLarge.copy(
                lineHeight = 24.sp,
                fontWeight = FontWeight.Black
            ),
            color = Color.Black.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(28.dp))
        ShopSheetPrimaryButton(text = "XÁC NHẬN", enabled = true, onClick = onDismiss)
    }
}

@Composable
private fun ShopPurchaseItemPreviewCard(item: ShopItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33000000))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.size(59.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            if (item.oldPrice != null) {
                Text(
                    text = item.oldPrice.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280),
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            Text(text = "♦", color = Color(0xFFF44336), style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = item.price.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF44336)
            )
        }
    }
}

private fun buildWaterFreezeDescription(quantity: Int) = buildAnnotatedString {
    val body = descBodyStyle()
    val green = descGreenStyle()
    pushStyle(body); append("Một Water Freeze giúp bảo vệ chuỗi tưới nước của bạn cho 1 ngày. Bạn đang có "); pop()
    pushStyle(green); append("$quantity chưa sử dụng"); pop()
    pushStyle(body); append("."); pop()
}

private fun buildHackKnDescription(quantity: Int) = buildAnnotatedString {
    val body = descBodyStyle()
    val green = descGreenStyle()
    val unused = descQuantityStyle(quantity)
    pushStyle(body); append("Một Hack KN sẽ giúp bạn "); pop()
    pushStyle(green); append("x3 kinh nghiệm trong 30 phút"); pop()
    pushStyle(body); append(". Học thêm để nhận được 1 Hack KN. Bạn đang có"); pop()
    pushStyle(unused); append(" $quantity chưa sử dụng"); pop()
    pushStyle(body); append("."); pop()
}

private fun buildSieuKnDescription(quantity: Int) = buildAnnotatedString {
    val body = descBodyStyle()
    val green = descGreenStyle()
    val unused = descQuantityStyle(quantity)
    pushStyle(body); append("Một Siêu KN sẽ giúp bạn "); pop()
    pushStyle(green); append("x2 kinh nghiệm trong 15 phút"); pop()
    pushStyle(body); append(". Học thêm để nhận được 1 Siêu KN. Bạn đang có"); pop()
    pushStyle(unused); append(" $quantity chưa sử dụng"); pop()
    pushStyle(body); append("."); pop()
}

private fun descBodyStyle() = SpanStyle(
    color = Color(0xB3000000), fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = Nunito
)
private fun descGreenStyle() = SpanStyle(
    color = Color(0xFF89E219), fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = Nunito
)
private fun descQuantityStyle(quantity: Int) = SpanStyle(
    color = if (quantity > 0) Color(0xFF58CC02) else Color(0xFFFF383C),
    fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = Nunito
)

@Composable
private fun ShopSheetPrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF58CC02),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF58CC02).copy(alpha = 0.45f),
            disabledContentColor = Color.White.copy(alpha = 0.85f)
        ),
        contentPadding = PaddingValues(vertical = 12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun OwnedItemCard(
    modifier: Modifier = Modifier,
    item: OwnedItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.size(62.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "x${item.quantity}",
                style = MaterialTheme.typography.titleMedium,
                color = if (item.quantity > 0) Color(0xFF89E219) else Color(0xFFF44336)
            )
        }
    }
}

@Composable
private fun ShopItemRow(
    item: ShopItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.size(52.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF1F2937),
                modifier = Modifier.weight(1f)
            )
            if (item.oldPrice != null) {
                Text(
                    text = item.oldPrice.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280),
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(text = "♦", color = Color(0xFFF44336))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = item.price.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFF44336)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ShopScreenPreview() {
    PotagoTheme(dynamicColor = false) {
        ShopScreen(navController = rememberNavController())
    }
}

