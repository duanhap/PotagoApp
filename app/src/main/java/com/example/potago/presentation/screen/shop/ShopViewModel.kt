package com.example.potago.presentation.screen.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Item
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.GetItemsUseCase
import com.example.potago.domain.usecase.GetUserProfileUseCase
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
}

// Item types matching backend
object ItemType {
    const val WATER_STREAK = "water_streak"
    const val SUPER_XP = "super_xp"
    const val HACK_XP = "hack_xp"
}

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val purchaseItemUseCase: PurchaseItemUseCase,
    private val useItemUseCase: UseItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    private val _events = Channel<ShopEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load diamond balance
            when (val result = getUserProfileUseCase()) {
                is Result.Success -> _uiState.update { it.copy(diamond = result.data?.diamond ?: 0) }
                else -> {}
            }
        }
        viewModelScope.launch {
            // Load items
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
                    val (item, diamondRemaining) = result.data!!
                    _uiState.update {
                        it.copy(
                            items = UiState.Success(item),
                            diamond = diamondRemaining,
                            isActionLoading = false
                        )
                    }
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
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            when (val result = useItemUseCase(itemType)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            items = UiState.Success(result.data!!),
                            isActionLoading = false
                        )
                    }
                    _events.send(ShopEvent.ShowSnackbar("Đã sử dụng!"))
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
