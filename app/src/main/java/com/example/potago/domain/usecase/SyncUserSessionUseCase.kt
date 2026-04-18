package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.StreakRepository
import com.example.potago.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Fetch và lưu vào local cache toàn bộ dữ liệu session của user sau khi đăng nhập:
 * profile, settings, streak hiện tại, streak date hôm nay.
 * Repository impl đã tự save vào DataStore — use case chỉ cần gọi đúng thứ tự.
 */
class SyncUserSessionUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val streakRepository: StreakRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val profileResult = userRepository.getUserProfile()
        if (profileResult is Result.Error) return Result.Error(profileResult.message)

        userRepository.getUserSettings()   // lỗi settings không block login
        streakRepository.getCurrentStreak()
        streakRepository.getTodayStreakDate()

        return Result.Success(Unit)
    }
}