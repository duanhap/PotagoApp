package com.example.potago.presentation.screen.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.User
import com.example.potago.domain.usecase.GetMyRankingUseCase
import com.example.potago.domain.usecase.GetRankingTopUseCase
import com.example.potago.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RankUiState(
    val isLoading: Boolean = false,
    val topUsers: List<User> = emptyList(),
    val currentUser: User? = null,
    val myRank: Int? = null,
    val error: String? = null
)

@HiltViewModel
class RankViewModel @Inject constructor(
    private val getRankingTopUseCase: GetRankingTopUseCase,
    private val getMyRankingUseCase: GetMyRankingUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankUiState())
    val uiState: StateFlow<RankUiState> = _uiState.asStateFlow()

    init {
        loadRanking()
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getSavedUser().collect { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
            }
        }
    }

    fun loadRanking() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Call both APIs
            val topResult = getRankingTopUseCase()
            val myRankResult = getMyRankingUseCase()

            if (topResult is Result.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    topUsers = topResult.data,
                    myRank = if (myRankResult is Result.Success) myRankResult.data else null
                )
            } else if (topResult is Result.Error) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = topResult.message)
            }
        }
    }
}
