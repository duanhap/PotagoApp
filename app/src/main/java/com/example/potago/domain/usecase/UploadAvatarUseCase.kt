package com.example.potago.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.User
import com.example.potago.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UploadAvatarUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(imageUri: Uri): Result<User> {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
            val inputStream = contentResolver.openInputStream(imageUri)
                ?: return Result.Error("Không thể đọc ảnh")
            val imageBytes = inputStream.readBytes()
            inputStream.close()
            userRepository.uploadAvatar(imageBytes, mimeType)
        } catch (e: Exception) {
            Result.Error("Upload thất bại: ${e.message}")
        }
    }
}
