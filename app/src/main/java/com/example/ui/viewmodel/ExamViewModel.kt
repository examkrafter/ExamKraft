package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AttemptEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.UserProfileEntity
import com.example.data.remote.GeminiService
import com.example.data.repository.ExamRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Dashboard : Screen()
    data class Exam(val testId: Long) : Screen()
    data class Result(val attemptId: Long) : Screen()
    data object Analytics : Screen()
}

data class ActiveExamState(
    val test: TestEntity? = null,
    val questions: List<QuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val flaggedIndices: Set<Int> = emptySet(),
    val timeRemainingSeconds: Int = 0,
    val isTimerActive: Boolean = false,
    val isComplete: Boolean = false
)

data class QuotaUiState(
    val isPro: Boolean = false,
    val questionsGeneratedToday: Int = 0,
    val dailyLimit: Int = 20,
    val remainingToday: Int = 20
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val geminiService = GeminiService(application)
    private val repository = ExamRepository(database, geminiService)

    val allTests: StateFlow<List<TestEntity>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttempts: StateFlow<List<AttemptEntity>> = repository.allAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _examState = MutableStateFlow(ActiveExamState())
    val examState: StateFlow<ActiveExamState> = _examState.asStateFlow()

    private val _latestAttempt = MutableStateFlow<Pair<AttemptEntity, List<QuestionEntity>>?>(null)
    val latestAttempt: StateFlow<Pair<AttemptEntity, List<QuestionEntity>>?> = _latestAttempt.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationError = MutableStateFlow<String?>(null)
    val generationError: StateFlow<String?> = _generationError.asStateFlow()

    private val _showUploadDialog = MutableStateFlow(false)
    val showUploadDialog: StateFlow<Boolean> = _showUploadDialog.asStateFlow()

    private val _showProDialog = MutableStateFlow(false)
    val showProDialog: StateFlow<Boolean> = _showProDialog.asStateFlow()

    private var timerJob: Job? = null

    val quotaState: StateFlow<QuotaUiState> = combine(
        repository.userProfile,
        repository.getTodayQuota()
    ) { profile, quota ->
        val isPro = profile?.isPro == true
        val generated = quota?.questionsGenerated ?: 0
        val remaining = if (isPro) Int.MAX_VALUE else (20 - generated).coerceAtLeast(0)
        QuotaUiState(
            isPro = isPro,
            questionsGeneratedToday = generated,
            dailyLimit = 20,
            remainingToday = remaining
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuotaUiState())

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
        }
    }

    fun navigateTo(screen: Screen) {
        if (screen !is Screen.Exam) {
            timerJob?.cancel()
        }
        _currentScreen.value = screen
    }

    fun openUploadDialog() {
        _generationError.value = null
        _showUploadDialog.value = true
    }

    fun closeUploadDialog() {
        _showUploadDialog.value = false
    }

    fun openProDialog() {
        _showProDialog.value = true
    }

    fun closeProDialog() {
        _showProDialog.value = false
    }

    fun createTestFromPdf(
        pdfUri: Uri?,
        pdfBytes: ByteArray?,
        title: String,
        documentName: String,
        questionCount: Int,
        difficulty: String,
        sampleText: String? = null
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            _generationError.value = null
            val result = repository.createTestFromPdf(
                pdfUri = pdfUri,
                pdfBytes = pdfBytes,
                title = title,
                documentName = documentName,
                questionCount = questionCount,
                difficulty = difficulty,
                extractedTextFallback = sampleText
            )
            _isGenerating.value = false
            result.onSuccess { newTestId ->
                _showUploadDialog.value = false
                startExam(newTestId)
            }.onFailure { e ->
                _generationError.value = e.message ?: "Failed to generate test from PDF"
            }
        }
    }

    fun startExam(testId: Long) {
        viewModelScope.launch {
            val test = allTests.value.firstOrNull { it.id == testId }
                ?: database.testDao().getTestById(testId)
            val questions = repository.getQuestionsForTest(testId)
            if (test != null && questions.isNotEmpty()) {
                val durationSeconds = test.targetDurationMinutes * 60
                _examState.value = ActiveExamState(
                    test = test,
                    questions = questions,
                    currentIndex = 0,
                    selectedAnswers = emptyMap(),
                    flaggedIndices = emptySet(),
                    timeRemainingSeconds = durationSeconds,
                    isTimerActive = true,
                    isComplete = false
                )
                startTimer()
                _currentScreen.value = Screen.Exam(testId)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_examState.value.timeRemainingSeconds > 0 && _examState.value.isTimerActive) {
                delay(1000L)
                val remaining = _examState.value.timeRemainingSeconds - 1
                _examState.value = _examState.value.copy(timeRemainingSeconds = remaining)
                if (remaining <= 0) {
                    submitExam()
                    break
                }
            }
        }
    }

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        val updated = _examState.value.selectedAnswers.toMutableMap()
        updated[questionIndex] = optionIndex
        _examState.value = _examState.value.copy(selectedAnswers = updated)
    }

    fun toggleFlagQuestion(questionIndex: Int) {
        val flags = _examState.value.flaggedIndices.toMutableSet()
        if (flags.contains(questionIndex)) {
            flags.remove(questionIndex)
        } else {
            flags.add(questionIndex)
        }
        _examState.value = _examState.value.copy(flaggedIndices = flags)
    }

    fun setQuestionIndex(index: Int) {
        if (index in _examState.value.questions.indices) {
            _examState.value = _examState.value.copy(currentIndex = index)
        }
    }

    fun submitExam() {
        timerJob?.cancel()
        val state = _examState.value
        val test = state.test ?: return
        val questions = state.questions
        if (questions.isEmpty()) return

        var score = 0
        for ((idx, question) in questions.withIndex()) {
            val selected = state.selectedAnswers[idx]
            if (selected != null && selected == question.correctOptionIndex) {
                score++
            }
        }

        val totalTime = test.targetDurationMinutes * 60
        val timeSpent = (totalTime - state.timeRemainingSeconds).coerceAtLeast(1)

        viewModelScope.launch {
            val attemptId = repository.recordAttempt(
                testId = test.id,
                testTitle = test.title,
                score = score,
                totalQuestions = questions.size,
                timeSpentSeconds = timeSpent,
                answersMap = state.selectedAnswers
            )
            val attempt = database.attemptDao().getAttemptById(attemptId)
            if (attempt != null) {
                _latestAttempt.value = Pair(attempt, questions)
                _examState.value = state.copy(isTimerActive = false, isComplete = true)
                _currentScreen.value = Screen.Result(attemptId)
            }
        }
    }

    fun viewAttemptResult(attempt: AttemptEntity) {
        viewModelScope.launch {
            val questions = repository.getQuestionsForTest(attempt.testId)
            _latestAttempt.value = Pair(attempt, questions)
            _currentScreen.value = Screen.Result(attempt.id)
        }
    }

    fun deleteTest(testId: Long) {
        viewModelScope.launch {
            repository.deleteTest(testId)
        }
    }

    fun activatePro(code: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.activateProStatus(code)
            if (success) {
                _showProDialog.value = false
            }
            onResult(success)
        }
    }
}
