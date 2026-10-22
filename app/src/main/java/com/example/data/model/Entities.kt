package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val sourceDocumentName: String,
    val difficulty: String, // "Easy", "Medium", "Hard", "Mixed"
    val questionCount: Int,
    val targetDurationMinutes: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = TestEntity::class,
            parentColumns = ["id"],
            childColumns = ["testId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["testId"])]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val testId: Long,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0 for A, 1 for B, 2 for C, 3 for D
    val explanation: String
)

@Entity(
    tableName = "attempts",
    foreignKeys = [
        ForeignKey(
            entity = TestEntity::class,
            parentColumns = ["id"],
            childColumns = ["testId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["testId"])]
)
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val testId: Long,
    val testTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val timeSpentSeconds: Int,
    val attemptedAt: Long = System.currentTimeMillis(),
    val answersJson: String // Serialized Map<Int, Int> representing questionIndex -> selectedOptionIndex
)

@Entity(tableName = "daily_quota")
data class DailyQuotaEntity(
    @PrimaryKey
    val dateString: String, // e.g. "2026-10-22"
    val questionsGenerated: Int
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val isPro: Boolean = false,
    val proActivatedAt: Long? = null,
    val activationCode: String? = null
)
