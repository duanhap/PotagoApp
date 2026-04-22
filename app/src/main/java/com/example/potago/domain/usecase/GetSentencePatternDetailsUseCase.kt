package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.model.SetencePattern
import com.example.potago.domain.repository.SentencePatternRepository
import com.example.potago.domain.repository.SentenceRepository
import javax.inject.Inject

data class SentencePatternDetails(
    val pattern: SetencePattern,
    val sentences: List<Setence>
)

class GetSentencePatternDetailsUseCase @Inject constructor(
    private val sentencePatternRepository: SentencePatternRepository,
    private val sentenceRepository: SentenceRepository
) {
    suspend operator fun invoke(patternId: Int): Result<SentencePatternDetails> {
        val patternResult = sentencePatternRepository.getSentencePatternById(patternId)
        if (patternResult is Result.Error) return Result.Error(patternResult.message)
        
        val sentencesResult = sentenceRepository.getSentencesByPatternId(patternId)
        if (sentencesResult is Result.Error) return Result.Error(sentencesResult.message)
        
        val pattern = (patternResult as Result.Success).data
        val sentences = (sentencesResult as Result.Success).data
        
        return Result.Success(
            SentencePatternDetails(
                pattern = pattern,
                sentences = sentences
            )
        )
    }
}
