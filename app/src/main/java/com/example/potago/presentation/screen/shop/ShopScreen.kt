package com.example.potago.presentation.screen.shop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.potago.domain.model.ActiveItemSession
import com.example.potago.presentation.screen.UiState
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.potago.presentation.screen.home.HomeUiState
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Nunito
import com.example.potago.presentation.ui.theme.PotagoTheme

// Chỉnh ảnh thủ công: mỗi chỗ một drawable riêng (không dùng chung trong composable).
private val shopHeroLeftRes = R.drawable.ic_twemoji_umbrella_on_ground
private val shopHeroRightRes = R.drawable.ic_icon_park_outline_sun

private val notoBigIce = R.drawable.noto_ice_big

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
    data class ItemConflict(val activeItemType: String) : ShopActiveSheet()
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
                is ShopEvent.ShowConflictSheet -> activeSheet =
                    ShopActiveSheet.ItemConflict(event.activeItemType)
            }
        }
    }

    val item = (uiState.items as? UiState.Success)?.data
    val ownedItems = listOf(
        OwnedItem(
            "Water Freeze",
            item?.waterStreak ?: 0,
            R.drawable.noto_ice,
            OwnedItemKind.WaterFreeze
        ),
        OwnedItem("Siêu KN", item?.superExperience ?: 0, R.drawable.bag_kn, OwnedItemKind.SieuKn),
        OwnedItem("Hack KN", item?.hackExperience ?: 0, R.drawable.bag_hack, OwnedItemKind.HackKn)
    )
    val streakItems = listOf(
        ShopItem(
            "Water Freeze",
            R.drawable.noto_ice,
            200,
            itemType = ItemType.WATER_STREAK,
            quantity = 1
        )
    )
    val superXpItems = listOf(
        ShopItem("Single", R.drawable.bag_kn, 500, itemType = ItemType.SUPER_XP, quantity = 1),
        ShopItem(
            "3 pack",
            R.drawable.bag_3pack,
            1200,
            1500,
            itemType = ItemType.SUPER_XP,
            quantity = 3
        ),
        ShopItem(
            "5 pack",
            R.drawable.bag_5pack,
            2000,
            2500,
            itemType = ItemType.SUPER_XP,
            quantity = 5
        )
    )
    Scaffold(
        topBar = {
            TopAppBar(
                diamondCount = uiState.diamond,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(modifier = Modifier.height(80.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                ) {
                    Image(
                        painter = painterResource(id = shopHeroLeftRes),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    Image(
                        painter = painterResource(shopHeroRightRes),
                        contentDescription = null,
                        modifier = Modifier.size(108.dp),
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
                    ShopItem(
                        "Hack KN",
                        R.drawable.bag_hackson,
                        300,
                        itemType = ItemType.HACK_XP,
                        quantity = 1
                    )
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
                //ShopSection("Hack KN", hackKnItems, uiState.diamond, onShopItemClick)
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

                        is ShopActiveSheet.ItemConflict -> ItemConflictBottomSheetContent(
                            onDismiss = { activeSheet = null }
                        )
                    }
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
private fun TopAppBar(
    diamondCount: Int,
    onBackClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color(0xFFFFFFFF)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {

            // ✅ Row chỉ còn Text → quyết định height
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Cửa hàng",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(start = 45.dp)
                )
                Row(
                    modifier = Modifier
                        .height(34.dp)
                        .background(
                            color = Color(0xFFFFE3E0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, Color(0x40F44336), RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_ruby_detailed_video_screen),
                        contentDescription = "Setting",
                        modifier = Modifier.scale(1f),
                        tint = Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = diamondCount.toString(),
                        color = Color(0xFFF44336),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // 🔥 BackButton overlay
            Box(
                modifier = Modifier.matchParentSize()
            ) {
                BackButtonShopScreen(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .wrapContentSize()
                )
            }
        }
    }
}

@Composable
private fun BackButtonShopScreen(
    onClick: () -> Unit,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        label = "icon_scale"
    )

    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier.offset(x = -10.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_library_add_button),
            contentDescription = "Back",
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .rotate(45f)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
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
        OwnedItemKind.WaterFreeze -> 200.dp
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
            Image(
                painter = painterResource(id = if (item.kind == OwnedItemKind.WaterFreeze) notoBigIce else item.imageRes),
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
            Spacer(modifier = Modifier.width(40.dp))
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_looking_mascot),
                contentDescription = null,
                modifier = Modifier
                    .scale(0.8f)
                    .offset(x = -40.dp),
            )
            Surface(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .offset(x = -20.dp),
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
        border = BorderStroke(1.dp, Color(0x33000000))
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
            //Text(text = "♦", color = Color(0xFFF44336), style = MaterialTheme.typography.bodyLarge)
            Icon(
                painter = painterResource(id = R.drawable.ic_ruby_detailed_video_screen),
                contentDescription = "Setting",
                modifier = Modifier.scale(1f),
                tint = Color(0xFFF44336)
            )
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
fun ShopSheetPrimaryButton(
    text: String = "XÁC NHẬN",
    enabled: Boolean,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = ""
    )
    val animatedHeight by animateDpAsState(
        targetValue = if (isPressed) 56.dp else 53.dp,
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .height(56.dp)
            .background(
                if (enabled) Color(0xFF46A302) else Color(0xFFABCF7E),
                RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .background(
                    if (enabled) Color(0xFF58CC02) else Color(0xFFB7E37E),
                    RoundedCornerShape(16.dp)
                )
                .pointerInput(enabled) {
                    detectTapGestures(
                        onPress = {
                            if (!enabled) return@detectTapGestures
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            if (enabled) {
                                onClick()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
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
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "x${item.quantity}",
                style = MaterialTheme.typography.labelLarge,
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
            Icon(
                painter = painterResource(id = R.drawable.ic_ruby_detailed_video_screen),
                contentDescription = "Setting",
                modifier = Modifier.scale(1f),
                tint = Color(0xFFF44336)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = item.price.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFF44336)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PurchaseInsufficientBottomSheetContent() {
    PurchaseInsufficientBottomSheetContent(onDismiss = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ItemConflictBottomSheetContentPreview() {
    ItemConflictBottomSheetContent(onDismiss = {})
}


// --- Active Item Badge ---
@Composable
fun ActiveItemBadge(
    session: ActiveItemSession,
    remainingMs: Long,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemLabel = when (session.itemType) {
        ItemType.SUPER_XP -> "X2 kinh nghiệm"
        ItemType.HACK_XP -> "X3 kinh nghiệm"
        else -> session.itemType.toString()
    }

    val minutes = (remainingMs / 1000 / 60).toInt()
    val seconds = (remainingMs / 1000 % 60).toInt()
    val timeText = "%02d:%02d".format(minutes, seconds)


    // Hiệu ứng nhún
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        label = "icon_scale"
    )

    Row(
        modifier = modifier
            .animateContentSize() // 🔥 quan trọng
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF1A3D01),
                        Color(0xFF58CC02)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(end = if (expanded) 15.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔥 IMAGE (luôn hiển thị)
        Image(
            painter = painterResource(id = R.drawable.ic_using_item),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp) // to hơn row -> nổi lên
                .clickable { onClick() }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clickable(
                    interactionSource = interactionSource, // 🔥 GẮN VÀO ĐÂY
                    indication = ripple(
                        bounded = true,
                        radius = 25.dp,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                ) {
                    onClick()
                },

        )

        // 🔥 CONTENT (chỉ hiện khi expanded)
        AnimatedVisibility(visible = expanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = itemLabel,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = timeText,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

// --- Item Conflict Bottom Sheet ---
@Composable
private fun ItemConflictBottomSheetContent(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sick_mascot_shop_screen),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 20.dp),
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "Chóng đầu đau mặt quá!@%*^",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B5563),
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 12.dp)
                )
            }
        }
        Text(
            text = "Mỗi lần chỉ sử dụng được 1 loại buff.\nQuá liều đôi khi sẽ không tốt cho potato.",
            style = MaterialTheme.typography.titleLarge.copy(
                lineHeight = 26.sp,
                fontWeight = FontWeight.Black
            ),
            color = Color.Black.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        ShopSheetPrimaryButton(text = "XÁC NHẬN", enabled = true, onClick = onDismiss)
    }
}
