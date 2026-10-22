package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedError
import com.example.ui.viewmodel.ActiveExamState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    examState: ActiveExamState,
    onSelectAnswer: (questionIndex: Int, optionIndex: Int) -> Unit,
    onToggleFlag: (questionIndex: Int) -> Unit,
    onSetQuestionIndex: (index: Int) -> Unit,
    onSubmitExam: () -> Unit,
    onExitExam: () -> Unit
) {
    var showSubmitConfirmDialog by remember { mutableStateOf(false) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitConfirmDialog = true
    }

    val questions = examState.questions
    val currentIndex = examState.currentIndex
    val currentQuestion = questions.getOrNull(currentIndex)
    val totalCount = questions.size

    val answeredCount = examState.selectedAnswers.size
    val flaggedCount = examState.flaggedIndices.size
    val unansweredCount = (totalCount - answeredCount).coerceAtLeast(0)

    val minutes = examState.timeRemainingSeconds / 60
    val seconds = examState.timeRemainingSeconds % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    val isLowTime = examState.timeRemainingSeconds < 120

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = examState.test?.title ?: "Banking Exam",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (isLowTime) RedError else NavyPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isLowTime) RedError else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showExitConfirmDialog = true }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Exam")
                    }
                },
                actions = {
                    Button(
                        onClick = { showSubmitConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Submit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onSetQuestionIndex(currentIndex - 1) },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }

                    if (examState.selectedAnswers.containsKey(currentIndex)) {
                        TextButton(
                            onClick = {
                                // deselect by removing answer
                                onSelectAnswer(currentIndex, -1)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Clear", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    if (currentIndex < totalCount - 1) {
                        Button(
                            onClick = { onSetQuestionIndex(currentIndex + 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = { showSubmitConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Submit Test")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Horizontal Question Navigation Strip
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(questions) { idx, _ ->
                    val isCurrent = idx == currentIndex
                    val isAnswered = examState.selectedAnswers.containsKey(idx) && (examState.selectedAnswers[idx] ?: -1) >= 0
                    val isFlagged = examState.flaggedIndices.contains(idx)

                    val bgColor by animateColorAsState(
                        targetValue = when {
                            isCurrent -> NavyPrimary
                            isAnswered -> GreenSuccess.copy(alpha = 0.85f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        label = "pillBg"
                    )

                    val textColor = when {
                        isCurrent || isAnswered -> Color.White
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .border(
                                width = if (isFlagged) 2.dp else 1.dp,
                                color = if (isFlagged) GoldAccent else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onSetQuestionIndex(idx) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = textColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Question Body
            if (currentQuestion != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Question Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Question ${currentIndex + 1} of $totalCount",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = examState.test?.difficulty ?: "Medium",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                IconButton(onClick = { onToggleFlag(currentIndex) }) {
                                    val isFlagged = examState.flaggedIndices.contains(currentIndex)
                                    Icon(
                                        imageVector = if (isFlagged) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Flag Question",
                                        tint = if (isFlagged) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = currentQuestion.questionText,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "SELECT AN OPTION:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4 Options
                    val options = listOf(
                        Pair("A", currentQuestion.optionA),
                        Pair("B", currentQuestion.optionB),
                        Pair("C", currentQuestion.optionC),
                        Pair("D", currentQuestion.optionD)
                    )

                    val selectedOption = examState.selectedAnswers[currentIndex] ?: -1

                    options.forEachIndexed { optIndex, (label, optionText) ->
                        val isSelected = selectedOption == optIndex

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectAnswer(currentIndex, optIndex) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NavyPrimary.copy(alpha = 0.08f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                width = if (isSelected) 2.dp else 1.dp
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmDialog = false },
            title = { Text("Submit Exam Paper?") },
            text = {
                Column {
                    Text("Are you sure you want to finalize and submit your test?")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• Answered Questions:")
                        Text("$answeredCount / $totalCount", fontWeight = FontWeight.Bold, color = GreenSuccess)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• Unanswered Questions:")
                        Text("$unansweredCount", fontWeight = FontWeight.Bold, color = if (unansweredCount > 0) RedError else GreenSuccess)
                    }
                    if (flaggedCount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Flagged for Review:")
                            Text("$flaggedCount", fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmDialog = false
                        onSubmitExam()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                ) {
                    Text("Yes, Submit Now")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSubmitConfirmDialog = false }) {
                    Text("Continue Test")
                }
            }
        )
    }

    // Exit Confirmation Dialog
    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = { Text("Quit Test Session?") },
            text = { Text("If you exit now, your current test progress will be lost. Do you want to leave?") },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmDialog = false
                        onExitExam()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedError)
                ) {
                    Text("Quit Test")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitConfirmDialog = false }) {
                    Text("Resume")
                }
            }
        )
    }
}
