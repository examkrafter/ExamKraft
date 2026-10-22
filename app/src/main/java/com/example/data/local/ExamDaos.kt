package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.AttemptEntity
import com.example.data.model.DailyQuotaEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Query("SELECT * FROM tests ORDER BY createdAt DESC")
    fun getAllTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE id = :testId")
    suspend fun getTestById(testId: Long): TestEntity?

    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY id ASC")
    fun getQuestionsForTest(testId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY id ASC")
    suspend fun getQuestionsListForTest(testId: Long): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Transaction
    suspend fun insertTestWithQuestions(test: TestEntity, questions: List<QuestionEntity>): Long {
        val testId = insertTest(test)
        val linkedQuestions = questions.map { it.copy(testId = testId) }
        insertQuestions(linkedQuestions)
        return testId
    }

    @Query("DELETE FROM tests WHERE id = :testId")
    suspend fun deleteTest(testId: Long)
}

@Dao
interface AttemptDao {
    @Query("SELECT * FROM attempts ORDER BY attemptedAt DESC")
    fun getAllAttempts(): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts WHERE testId = :testId ORDER BY attemptedAt DESC")
    fun getAttemptsForTest(testId: Long): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts WHERE id = :attemptId")
    suspend fun getAttemptById(attemptId: Long): AttemptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: AttemptEntity): Long

    @Query("SELECT COUNT(*) FROM attempts")
    fun getAttemptCount(): Flow<Int>
}

@Dao
interface QuotaDao {
    @Query("SELECT * FROM daily_quota WHERE dateString = :dateString")
    fun getQuotaForDate(dateString: String): Flow<DailyQuotaEntity?>

    @Query("SELECT * FROM daily_quota WHERE dateString = :dateString")
    suspend fun getQuotaForDateDirect(dateString: String): DailyQuotaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setQuota(quota: DailyQuotaEntity)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileDirect(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUserProfile(profile: UserProfileEntity)
}
