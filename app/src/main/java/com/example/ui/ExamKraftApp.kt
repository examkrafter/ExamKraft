package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.ProUpgradeDialog
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.UploadPdfDialog
import com.example.ui.viewmodel.ExamViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun ExamKraftApp(viewModel: ExamViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val allTests by viewModel.allTests.collectAsStateWithLifecycle()
    val allAttempts by viewModel.allAttempts.collectAsStateWithLifecycle()
    val quotaState by viewModel.quotaState.collectAsStateWithLifecycle()
    val examState by viewModel.examState.collectAsStateWithLifecycle()
    val latestAttempt by viewModel.latestAttempt.collectAsStateWithLifecycle()

    val showUploadDialog by viewModel.showUploadDialog.collectAsStateWithLifecycle()
    val showProDialog by viewModel.showProDialog.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generationError by viewModel.generationError.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
            is Screen.Dashboard -> {
                DashboardScreen(
                    tests = allTests,
                    attempts = allAttempts,
                    quotaState = quotaState,
                    onOpenUploadDialog = { viewModel.openUploadDialog() },
                    onOpenProDialog = { viewModel.openProDialog() },
                    onStartTest = { testId -> viewModel.startExam(testId) },
                    onViewAttempt = { attempt -> viewModel.viewAttemptResult(attempt) },
                    onViewAllAnalytics = { viewModel.navigateTo(Screen.Analytics) },
                    onDeleteTest = { testId -> viewModel.deleteTest(testId) }
                )
            }

            is Screen.Exam -> {
                ExamScreen(
                    examState = examState,
                    onSelectAnswer = { qIdx, optIdx -> viewModel.selectAnswer(qIdx, optIdx) },
                    onToggleFlag = { qIdx -> viewModel.toggleFlagQuestion(qIdx) },
                    onSetQuestionIndex = { idx -> viewModel.setQuestionIndex(idx) },
                    onSubmitExam = { viewModel.submitExam() },
                    onExitExam = { viewModel.navigateTo(Screen.Dashboard) }
                )
            }

            is Screen.Result -> {
                val pair = latestAttempt
                if (pair != null) {
                    ResultScreen(
                        attempt = pair.first,
                        questions = pair.second,
                        onRetakeTest = { testId -> viewModel.startExam(testId) },
                        onViewAnalytics = { viewModel.navigateTo(Screen.Analytics) },
                        onBackToDashboard = { viewModel.navigateTo(Screen.Dashboard) }
                    )
                } else {
                    viewModel.navigateTo(Screen.Dashboard)
                }
            }

            is Screen.Analytics -> {
                AnalyticsScreen(
                    attempts = allAttempts,
                    onSelectAttempt = { attempt -> viewModel.viewAttemptResult(attempt) },
                    onBack = { viewModel.navigateTo(Screen.Dashboard) }
                )
            }
        }

        // Upload PDF Dialog Modal
        if (showUploadDialog) {
            UploadPdfDialog(
                quotaState = quotaState,
                isGenerating = isGenerating,
                errorMessage = generationError,
                onDismiss = { viewModel.closeUploadDialog() },
                onGenerate = { pdfUri, pdfBytes, title, documentName, questionCount, difficulty, sampleText ->
                    viewModel.createTestFromPdf(
                        pdfUri = pdfUri,
                        pdfBytes = pdfBytes,
                        title = title,
                        documentName = documentName,
                        questionCount = questionCount,
                        difficulty = difficulty,
                        sampleText = sampleText
                    )
                }
            )
        }

        // Pro Upgrade Dialog Modal
        if (showProDialog) {
            ProUpgradeDialog(
                isPro = quotaState.isPro,
                onDismiss = { viewModel.closeProDialog() },
                onActivateCode = { code, callback ->
                    viewModel.activatePro(code, callback)
                }
            )
        }
    }
}
