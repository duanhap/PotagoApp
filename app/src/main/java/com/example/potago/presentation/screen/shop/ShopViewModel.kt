package com.example.potago.presentation.screen.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Item
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.ActivateItemResult
import com.example.potago.domain.usecase.ActivateItemUseCase
import com.example.potago.domain.usecase.GetItemsUseCase
import com.example.potago.domain.usecase.ObserveUserUseCase
import com.example.potago.domain.usecase.PurchaseItemUseCase
import com.example.potago.domain.usecase.UseItemUseCase
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopUiState(
    val items: UiState<Item> = UiState.Loading,
    val diamond: Int = 0,
    val isActionLoading: Boolean = false
)

sealed class ShopEvent {
    data class ShowSnackbar(val message: String) : ShopEvent()
    data class ShowConflictSheet(val activeItemType: String) : ShopEvent()
}

object ItemType {
    const val WATER_STREAK = "water_streak"
    const val SUPER_XP = "super_xp"
    const val HACK_XP = "hack_xp"
}

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val observeUserUseCase: ObserveUserUseCase,
    private val purchaseItemUseCase: PurchaseItemUseCase,
    private val useItemUseCase: UseItemUseCase,
    private val activateItemUseCase: ActivateItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    private val _events = Channel<ShopEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeUserUseCase().collect { user ->
                _uiState.update { it.copy(diamond = user?.diamond ?: 0) }
            }
        }
        viewModelScope.launch {
            when (val result = getItemsUseCase()) {
                is Result.Success -> _uiState.update { it.copy(items = UiState.Success(result.data)) }
                is Result.Error -> _uiState.update { it.copy(items = UiState.Error(result.message ?: "Lỗi")) }
                else -> {}
            }
        }
    }

    fun purchase(itemType: String, quantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            when (val result = purchaseItemUseCase(itemType, quantity)) {
                is Result.Success -> {
                    val (item, _) = result.data!!
                    _uiState.update { it.copy(items = UiState.Success(item), isActionLoading = false) }
                    _events.send(ShopEvent.ShowSnackbar("Mua thành công!"))
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isActionLoading = false) }
                    _events.send(ShopEvent.ShowSnackbar(result.message ?: "Mua thất bại"))
                }
                else -> _uiState.update { it.copy(isActionLoading = false) }
            }
        }
    }

    fun useItem(itemType: String) {
        if (itemType == ItemType.WATER_STREAK) {
            // Water Freeze không có session timer — gọi API bình thường
            viewModelScope.launch {
                _uiState.update { it.copy(isActionLoading = true) }
                when (val result = useItemUseCase(itemType)) {
                    is Result.Success -> {
                        _uiState.update { it.copy(items = UiState.Success(result.data!!), isActionLoading = false) }
                        _events.send(ShopEvent.ShowSnackbar("Đã sử dụng Water Freeze!"))
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isActionLoading = false) }
                        _events.send(ShopEvent.ShowSnackbar(result.message ?: "Thất bại"))
                    }
                    else -> _uiState.update { it.copy(isActionLoading = false) }
                }
            }
            return
        }

        // Siêu KN / Hack KN → activate session + gọi API
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            when (val activateResult = activateItemUseCase(itemType)) {
                is ActivateItemResult.ConflictWithOtherItem -> {
                    _uiState.update { it.copy(isActionLoading = false) }
                    _events.send(ShopEvent.ShowConflictSheet(activateResult.activeItemType))
                }
                is ActivateItemResult.Success -> {
                    when (val result = useItemUseCase(itemType)) {
                        is Result.Success -> {
                            _uiState.update { it.copy(items = UiState.Success(result.data!!), isActionLoading = false) }
                            _events.send(ShopEvent.ShowSnackbar("Đã kích hoạt!"))
                        }
                        is Result.Error -> {
                            _uiState.update { it.copy(isActionLoading = false) }
                            _events.send(ShopEvent.ShowSnackbar(result.message ?: "Thất bại"))
                        }
                        else -> _uiState.update { it.copy(isActionLoading = false) }
                    }
                }
            }
        }
    }
}
