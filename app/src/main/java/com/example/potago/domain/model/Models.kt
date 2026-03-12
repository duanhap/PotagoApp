package com.example.potago.domain.model

data class User(
    val id: Int = 0,
    val uid: String = "",
    val email: String? = "",
    val name: String = "",
    val experiencePoints: Int = 0,
    val diamond: Int = 0,
    val role: String = "User",
    val createdAt: String = "",
    val lastLogin: String? = null,
    val avatar: String? = null,
    val tokenFCM: String? = null
)

data class Streak(
    val id: Long = 0,
    val lengthStreak: Int = 0,
    val startDate: Int = 0, // DDL says int(10), maybe a timestamp
    val currentStreak: Boolean = false,
    val userId: Int = 0
)

data class WordSet(
    val id: Long = 0,
    val name: String = "",
    val description: String? = null,
    val createdAt: String = "",
    val isPublic: Boolean? = null,
    val definitionLanguageCode: String = "",
    val termLanguageCode: String = "",
    val updatedAt: String? = null,
    val lastOpened: String? = null,
    val userId: Int = 0
)

data class Word(
    val id: Long = 0,
    val term: String = "",
    val definition: String = "",
    val description: String? = null,
    val createdAt: String = "",
    val status: String = "",
    val wordSetId: Long? = null,
    val flashcardGameId: Long = 0,
    val matchGameId: Int = 0
)

data class FlashcardGame(
    val id: Long = 0,
    val mode: String = "",
    val updatedAt: String? = null,
    val wordSetId: Long = 0
)

data class UserAchievement(
    val id: Long = 0,
    val name: String = "",
    val description: String? = null,
    val earnedAt: String? = null,
    val image: String? = null,
    val userId: Int = 0
)

data class StreakDate(
    val id: Long = 0,
    val date: String = "",
    val protectedDate: Boolean? = null,
    val protectedBy: String? = null,
    val experiencePointsEarned: Int? = null,
    val streakId: Long = 0
)

data class MatchGame(
    val id: Int = 0,
    val createdAt: String = "",
    val completedTime: Int? = null,
    val wordSetId: Long = 0
)

data class SentencePattern(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val createdAt: String = "",
    val isPublic: Boolean? = null,
    val termLanguageCode: String = "",
    val definitionLanguageCode: String = "",
    val updatedAt: String? = null,
    val lastOpened: String? = null,
    val userId: Int = 0
)

data class Sentence(
    val id: Int = 0,
    val term: String = "",
    val definition: String = "",
    val createdAt: String = "",
    val status: String = "",
    val numberOfMistakes: Int? = null,
    val sentencePatternId: Int = 0
)

data class WritingGame(
    val id: Int = 0,
    val createdAt: String? = null,
    val completedTime: String? = null,
    val sentencePatternId: Int = 0
)

data class Setting(
    val id: Int = 0,
    val notification: Boolean = false,
    val language: String? = null,
    val userId: Int = 0
)

data class Item(
    val id: Int = 0,
    val waterStreak: Int = 0,
    val superExperience: Int = 0,
    val hackExperience: Int = 0,
    val userId: Int = 0
)

data class Video(
    val id: Int = 0,
    val title: String = "",
    val thumbnail: String = "",
    val sourceUrl: String = "",
    val lastOpened: String? = null,
    val typeVideo: Int? = null,
    val createdAt: String? = null,
    val userId: Int? = null
)

data class Subtitle(
    val id: Int = 0,
    val sourceUrl: Int = 0, // DDL says int, but usually it's a string URL? I'll follow DDL for now
    val videoId: Int = 0
)
