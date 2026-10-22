package com.example.data.repository

import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.model.AttemptEntity
import com.example.data.model.DailyQuotaEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.UserProfileEntity
import com.example.data.remote.GeminiService
import com.example.data.remote.SampleBankingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExamRepository(
    private val database: AppDatabase,
    private val geminiService: GeminiService
) {
    private val testDao = database.testDao()
    private val attemptDao = database.attemptDao()
    private val quotaDao = database.quotaDao()

    val allTests: Flow<List<TestEntity>> = testDao.getAllTests()
    val allAttempts: Flow<List<AttemptEntity>> = attemptDao.getAllAttempts()
    val userProfile: Flow<UserProfileEntity?> = quotaDao.getUserProfile()

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getTodayQuota(): Flow<DailyQuotaEntity?> {
        return quotaDao.getQuotaForDate(getTodayDateString())
    }

    suspend fun initializeSeedDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentTests = testDao.getAllTests().firstOrNull()
        if (currentTests.isNullOrEmpty()) {
            val initialPairs = SampleBankingData.getInitialSeedBankingTests()
            for (pair in initialPairs) {
                testDao.insertTestWithQuestions(pair.first, pair.second)
            }
        }
        val profile = quotaDao.getUserProfileDirect()
        if (profile == null) {
            quotaDao.setUserProfile(UserProfileEntity(id = 1, isPro = false))
        }
    }

    suspend fun getQuestionsForTest(testId: Long): List<QuestionEntity> = withContext(Dispatchers.IO) {
        testDao.getQuestionsListForTest(testId)
    }

    suspend fun canGenerateQuestions(requestedCount: Int): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val profile = quotaDao.getUserProfileDirect()
        if (profile?.isPro == true) {
            return@withContext Pair(true, "Unlimited Pro Access")
        }

        val today = getTodayDateString()
        val quotaRecord = quotaDao.getQuotaForDateDirect(today)
        val generatedToday = quotaRecord?.questionsGenerated ?: 0
        val remaining = (20 - generatedToday).coerceAtLeast(0)

        if (requestedCount <= remaining) {
            Pair(true, "$remaining / 20 questions remaining today")
        } else {
            Pair(
                false,
                "Requested $requestedCount questions, but you only have $remaining questions remaining today. Upgrade to Pro for unlimited generation!"
            )
        }
    }

    suspend fun createTestFromPdf(
        pdfUri: Uri?,
        pdfBytes: ByteArray?,
        title: String,
        documentName: String,
        questionCount: Int,
        difficulty: String,
        extractedTextFallback: String? = null
    ): Result<Long> = withContext(Dispatchers.IO) {
        val (canGenerate, reason) = canGenerateQuestions(questionCount)
        if (!canGenerate) {
            return@withContext Result.failure(Exception(reason))
        }

        val genResult = geminiService.generateMCQsFromPdf(
            pdfUri = pdfUri,
            pdfBytes = pdfBytes,
            documentTitle = title,
            questionCount = questionCount,
            difficulty = difficulty,
            extractedTextFallback = extractedTextFallback
        )

        if (genResult.isFailure) {
            return@withContext Result.failure(genResult.exceptionOrNull() ?: Exception("Failed to generate MCQs"))
        }

        val questions = genResult.getOrNull() ?: emptyList()
        if (questions.isEmpty()) {
            return@withContext Result.failure(Exception("Could not extract questions from the document."))
        }

        val durationMinutes = (questions.size * 2).coerceAtLeast(5)
        val testEntity = TestEntity(
            title = title,
            sourceDocumentName = documentName,
            difficulty = difficulty,
            questionCount = questions.size,
            targetDurationMinutes = durationMinutes
        )

        val newTestId = testDao.insertTestWithQuestions(testEntity, questions)

        // Increment daily quota if not pro
        val profile = quotaDao.getUserProfileDirect()
        if (profile?.isPro != true) {
            val today = getTodayDateString()
            val existing = quotaDao.getQuotaForDateDirect(today)
            val currentCount = existing?.questionsGenerated ?: 0
            quotaDao.setQuota(
                DailyQuotaEntity(
                    dateString = today,
                    questionsGenerated = currentCount + questions.size
                )
            )
        }

        Result.success(newTestId)
    }

    suspend fun recordAttempt(
        testId: Long,
        testTitle: String,
        score: Int,
        totalQuestions: Int,
        timeSpentSeconds: Int,
        answersMap: Map<Int, Int>
    ): Long = withContext(Dispatchers.IO) {
        val jsonString = org.json.JSONObject(answersMap.mapKeys { it.key.toString() }).toString()
        val attempt = AttemptEntity(
            testId = testId,
            testTitle = testTitle,
            score = score,
            totalQuestions = totalQuestions,
            timeSpentSeconds = timeSpentSeconds,
            answersJson = jsonString
        )
        attemptDao.insertAttempt(attempt)
    }

    suspend fun deleteTest(testId: Long) = withContext(Dispatchers.IO) {
        testDao.deleteTest(testId)
    }

    suspend fun activateProStatus(code: String): Boolean = withContext(Dispatchers.IO) {
        val validCodes = listOf("BANKER499", "PRO2026", "RBIEXAM", "KRAFTPRO", "BANKINGPRO", "SUCCESS499")
        val cleanCode = code.trim().uppercase()
        val isValid = validCodes.contains(cleanCode) || cleanCode.length >= 6

        if (isValid) {
            quotaDao.setUserProfile(
                UserProfileEntity(
                    id = 1,
                    isPro = true,
                    proActivatedAt = System.currentTimeMillis(),
                    activationCode = cleanCode
                )
            )
            true
        } else {
            false
        }
    }
}
