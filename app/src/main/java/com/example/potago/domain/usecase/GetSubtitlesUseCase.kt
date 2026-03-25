package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Subtitle
import com.example.potago.domain.repository.VideoRepository
import javax.inject.Inject

class GetSubtitlesUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: Int): Result<List<Subtitle>> {
        return videoRepository.getSubtitles(videoId)
    }
}
